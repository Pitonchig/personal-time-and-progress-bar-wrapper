package net.thumbtack.ptpb.wrapper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.thumbtack.ptpb.wrapper.client.item.DueDto;
import net.thumbtack.ptpb.wrapper.client.item.ItemDto;
import net.thumbtack.ptpb.wrapper.client.project.ProjectDto;
import net.thumbtack.ptpb.wrapper.client.syncdata.*;
import net.thumbtack.ptpb.wrapper.common.ErrorCode;
import net.thumbtack.ptpb.wrapper.common.PtpbException;
import net.thumbtack.ptpb.wrapper.db.mapper.Resource;
import net.thumbtack.ptpb.wrapper.db.mapper.ResourceDao;
import net.thumbtack.ptpb.wrapper.db.sync.Sync;
import net.thumbtack.ptpb.wrapper.db.sync.SyncDao;
import net.thumbtack.ptpb.wrapper.db.todoist.Todoist;
import net.thumbtack.ptpb.wrapper.db.todoist.TodoistDao;
import net.thumbtack.ptpb.wrapper.service.synchronization.ResponseWrapper;
import net.thumbtack.ptpb.wrapper.service.synchronization.dto.ItemAmqpDto;
import net.thumbtack.ptpb.wrapper.service.synchronization.dto.ProjectAmqpDto;
import net.thumbtack.ptpb.wrapper.service.synchronization.requests.SyncProjectsAmqpRequest;
import net.thumbtack.ptpb.wrapper.service.synchronization.requests.SyncUserTokenAmqpRequest;
import net.thumbtack.ptpb.wrapper.service.synchronization.responses.SyncProjectsAmqpResponse;
import net.thumbtack.ptpb.wrapper.service.synchronization.responses.SyncUserTokenAmqpResponse;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static net.thumbtack.ptpb.wrapper.client.syncdata.TodoistResourcesTypes.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SynchronizationService {

    private final TodoistDao todoistDao;
    private final SyncDao syncDao;
    private final ResourceDao resourceDao;
    private final TodoistClientService todoistClientService;

    private final String DEFAULT_TOKEN = "*";

    public SyncUserTokenAmqpResponse syncUserToken(SyncUserTokenAmqpRequest request) throws JsonProcessingException, PtpbException {
        List<TodoistResourcesTypes> resources = new LinkedList<>();
        resources.add(USER);
        SyncResponse response = todoistClientService.getSyncData(request.getToken(), DEFAULT_TOKEN, resources);
        if (response == null) {
            throw new PtpbException(ErrorCode.TODOIST_SERVICE_TIMEOUT);
        }

        boolean isTokenValid = response.getUser() != null;
        Optional<Todoist> todoistOptional = todoistDao.getTodoistByUserUuid(request.getUserId());

        Todoist todoist = todoistOptional.orElseGet(Todoist::new);
        todoist.setUserId(request.getUserId());
        todoist.setToken((isTokenValid) ? request.getToken() : "");
        todoistDao.insertTodoist(todoist);

        Sync sync = Sync.builder()
                .userId(request.getUserId())
                .syncToken(response.getToken())
                .build();
        syncDao.updateSync(sync);

        return SyncUserTokenAmqpResponse.builder()
                .isValid(isTokenValid)
                .build();
    }

    public SyncProjectsAmqpResponse syncProjects(SyncProjectsAmqpRequest request) throws PtpbException, JsonProcessingException {
        Optional<Todoist> todoist = todoistDao.getTodoistByUserUuid(request.getUserId());
        if (todoist.isEmpty()) {
            throw new PtpbException(ErrorCode.TODOIST_TOKEN_NOT_FOUND);
        }

        String token = todoist.get().getToken();
        List<ProjectAmqpDto> projects = request.getProjects();
        if (request.isToTodoist()) {
            List<TodoistCommand> commands = new LinkedList<>();
            Map<String, String> uuidMapper = new HashMap<>(); //<tmpUui, ptpbUuid>;

            for (ProjectAmqpDto project : projects) {
                String commandProjectUuid = UUID.randomUUID().toString();

                Optional<Resource> projectResource = resourceDao.getResourceById(project.getId());
                boolean isProjectExist = projectResource.isPresent();
                if (isProjectExist) {
                    commands.addAll(createUpdateProjectCommands(uuidMapper, project, commandProjectUuid, projectResource.get().getTodoistId()));
                } else {
                    commands.addAll(createAddProjectCommands(uuidMapper, project, commandProjectUuid));
                }
            }
            log.debug("commands: " + commands);
            TodoistResponse todoistResponse = todoistClientService.postData(token, commands);
            if(todoistResponse.getSyncStatus() != null) {
                List<String> errors = todoistResponse.getSyncStatus().values().stream().filter(p -> !isOkStatus(p)).collect(Collectors.toList());
                if (!errors.isEmpty()) {
                    throw new PtpbException(ErrorCode.TODOIST_SYNC_ERROR);
                }
            }
            saveResourcesMap(todoistResponse.getTempIdMapping(), uuidMapper);
        }

        if (request.isFromTodoist()) {
            List<TodoistResourcesTypes> resources = new LinkedList<>();
            resources.add(PROJECTS);
            resources.add(ITEMS);
            SyncResponse response = todoistClientService.getSyncData(token, getSyncTokenByUserId(request.getUserId()), resources);

            projects = createProjectAmqpList(projects, response);
        }

        return SyncProjectsAmqpResponse.builder()
                .userId(request.getUserId())
                .fromTodoist(request.isFromTodoist())
                .toTodoist(request.isToTodoist())
                .projects(projects)
                .build();
    }


    private List<ProjectAmqpDto> createProjectAmqpList(List<ProjectAmqpDto> projects, SyncResponse response) {
        List<ProjectAmqpDto> projectAmqpDtoList = new LinkedList<>();
        for (ProjectDto projectDto : response.getProjects()) {
            Resource resource = getOrCreateResourceByTodoistId(projectDto.getId());
            if (projectDto.isDeleted()) continue;

            List<ItemAmqpDto> itemAmqpDtoList = createItemAmqpList(response.getItems(), projectDto.getId());
            ProjectAmqpDto projectAmqpDto = projects.stream()
                    .filter(p -> p.getId().equals(resource.getUuid()))
                    .findFirst()
                    .orElse(ProjectAmqpDto.builder()
                            .id(resource.getUuid())
                            .name(projectDto.getName())
                            .items(itemAmqpDtoList)
                            .isDeleted(projectDto.isDeleted())
                            .build());

            projectAmqpDtoList.add(projectAmqpDto);
        }
        return projectAmqpDtoList;
    }

    private Resource getOrCreateResourceByTodoistId(long todoistId) {
        Optional<Resource> resourceOptional = resourceDao.getResourcesByTodoistId(todoistId).stream().findFirst();
        Resource resource;
        if (resourceOptional.isEmpty()) {
            resource = Resource.builder()
                    .uuid(UUID.randomUUID().toString())
                    .todoistId(todoistId)
                    .build();
            resourceDao.insertResource(resource);
        } else {
            resource = resourceOptional.get();
        }
        return resource;
    }

    private ZonedDateTime parseZonedDateTime(String dt) {
        if (dt == null) {
            return null;
        }
        return ZonedDateTime.parse(dt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss['Z']").withZone(ZoneId.of("UTC")));
    }

    private List<ItemAmqpDto> createItemAmqpList(List<ItemDto> items, Long id) {
        List<ItemAmqpDto> itemAmqpDtoList = new LinkedList<>();

        items.stream()
                .filter(i -> !i.isDeleted() && i.getProjectId() == id)
                .collect(Collectors.toList())
                .forEach(i -> {
                    Resource resource = getOrCreateResourceByTodoistId(i.getId());
                    ZonedDateTime start = parseZonedDateTime(i.getDateAdded());
                    ZonedDateTime finish = parseZonedDateTime(i.getDue().getDate());
                    ZonedDateTime completion = parseZonedDateTime(i.getDateCompleted());
                    ItemAmqpDto item = ItemAmqpDto.builder()
                            .id(resource.getUuid())
                            .content(i.getContent())
                            .isCompleted(i.isChecked())
                            .isDeleted(i.isDeleted())
                            .start(start)
                            .finish(finish)
                            .completion(completion)
                            .build();
                    itemAmqpDtoList.add(item);
                });

        return itemAmqpDtoList;
    }

    private String getSyncTokenByUserId(String uuid) {
        return syncDao.getSyncTokenByUserId(uuid)
                .orElse(Sync.builder()
                        .userId(uuid)
                        .syncToken(DEFAULT_TOKEN)
                        .build())
                .getSyncToken();
    }

    private boolean isOkStatus(String status) {
        return status.equals("ok");
    }


    private void saveResourcesMap(Map<String, Long> tempIdMapping, Map<String, String> uuidMapper) {
        List<Resource> resourceList = new LinkedList<>();
        for (String key : tempIdMapping.keySet()) {
            Long todoistId = tempIdMapping.get(key);
            String ptpbUuid = uuidMapper.get(key);
            Resource resource = new Resource(ptpbUuid, todoistId);
            resourceList.add(resource);
        }
        resourceDao.insertResources(resourceList);
    }

    private List<TodoistCommand> createAddProjectCommands(Map<String, String> uuidMapper, ProjectAmqpDto project, String projectCommandUuid) {
        List<TodoistCommand> commands = new LinkedList<>();
        String COMMAND_TYPE_PROJECT_ADD = "project_add";
        String COMMAND_ARG_PROJECT_NAME = "name";
        String projectTempId = UUID.randomUUID().toString();
        uuidMapper.put(projectTempId, project.getId());

        TodoistCommand projectCommand = TodoistCommand.builder()
                .type(COMMAND_TYPE_PROJECT_ADD)
                .tempId(projectTempId)
                .uuid(projectCommandUuid)
                .arg(COMMAND_ARG_PROJECT_NAME, project.getName())
                .build();
        commands.add(projectCommand);

        for (ItemAmqpDto item : project.getItems()) {
            String itemCommandUuid = UUID.randomUUID().toString();
            commands.addAll(createAddItemCommand(uuidMapper, item, itemCommandUuid, projectTempId));
        }

        if (project.isDeleted()) {
            String projectDeleteCommandUuid = UUID.randomUUID().toString();
            commands.add(createDeleteProjectCommand(projectDeleteCommandUuid, projectTempId));
        }

        return commands;
    }

    private List<TodoistCommand> createUpdateProjectCommands(Map<String, String> uuidMapper, ProjectAmqpDto project, String commandUuid, long projectId) {
        List<TodoistCommand> commands = new LinkedList<>();
        String COMMAND_TYPE_PROJECT_UPDATE = "project_update";
        String COMMAND_ARG_PROJECT_NAME = "name";
        String COMMAND_ARG_PROJECT_ID = "id";

        TodoistCommand projectCommand = TodoistCommand.builder()
                .type(COMMAND_TYPE_PROJECT_UPDATE)
                .uuid(commandUuid)
                .arg(COMMAND_ARG_PROJECT_ID, projectId)
                .arg(COMMAND_ARG_PROJECT_NAME, project.getName())
                .build();
        commands.add(projectCommand);

        for (ItemAmqpDto item : project.getItems()) {
            String itemCommandUuid = UUID.randomUUID().toString();

            Optional<Resource> itemResource = resourceDao.getResourceById(item.getId());
            boolean isItemExist = itemResource.isPresent();
            if (isItemExist) {
                commands.addAll(createUpdateItemCommand(item, itemCommandUuid, itemResource.get().getTodoistId()));
            } else {
                commands.addAll(createAddItemCommand(uuidMapper, item, itemCommandUuid, projectId));
            }
        }

        if (project.isDeleted()) {
            String projectDeleteCommandUuid = UUID.randomUUID().toString();
            commands.add(createDeleteProjectCommand(projectDeleteCommandUuid, projectId));
        }

        return commands;

    }

    private List<TodoistCommand> createAddItemCommand(Map<String, String> uuidMapper, ItemAmqpDto item, String commandUuid, String projectTempUuid) {
        List<TodoistCommand> commands = new LinkedList<>();
        String COMMAND_TYPE_ITEM_ADD = "item_add";
        String COMMAND_ARG_CONTENT = "content";
        String COMMAND_ARG_PROJECT_ID = "project_id";
        String COMMAND_ARG_ITEM_DUE = "due";

        String tmpUuid = UUID.randomUUID().toString();
        uuidMapper.put(tmpUuid, item.getId());

        DueDto due = DueDto.builder()
                .isRecurring(false)
                .lang("en")
                .timezone("UTC")
                .date(item.getFinish().format(DateTimeFormatter.ISO_INSTANT))
                .build();

        TodoistCommand addItemCommand = TodoistCommand.builder()
                .type(COMMAND_TYPE_ITEM_ADD)
                .tempId(tmpUuid)
                .uuid(commandUuid)
                .arg(COMMAND_ARG_CONTENT, item.getContent())
                .arg(COMMAND_ARG_PROJECT_ID, projectTempUuid)
                .arg(COMMAND_ARG_ITEM_DUE, due)
                .build();
        commands.add(addItemCommand);

        if (item.isCompleted()) {
            String completeCommandUuid = UUID.randomUUID().toString();
            TodoistCommand completeItemCommand = createCompleteItemCommand(completeCommandUuid, tmpUuid);
            commands.add(completeItemCommand);
        }

        if (item.isDeleted()) {
            String deleteCommandUuid = UUID.randomUUID().toString();
            TodoistCommand deleteItemCommand = createDeleteItemCommand(deleteCommandUuid, tmpUuid);
            commands.add(deleteItemCommand);
        }

        return commands;
    }

    private List<TodoistCommand> createAddItemCommand(Map<String, String> uuidMapper, ItemAmqpDto item, String commandUuid, long projectId) {
        List<TodoistCommand> commands = new LinkedList<>();
        String COMMAND_TYPE_ITEM_ADD = "item_add";
        String COMMAND_ARG_CONTENT = "content";
        String COMMAND_ARG_PROJECT_ID = "project_id";
        String COMMAND_ARG_ITEM_DUE = "due";

        String tmpUuid = UUID.randomUUID().toString();
        uuidMapper.put(tmpUuid, item.getId());

        DueDto due = DueDto.builder()
                .isRecurring(false)
                .lang("en")
                .timezone("UTC")
                .date(item.getFinish().format(DateTimeFormatter.ISO_INSTANT))
                .build();

        TodoistCommand addItemCommand = TodoistCommand.builder()
                .type(COMMAND_TYPE_ITEM_ADD)
                .tempId(tmpUuid)
                .uuid(commandUuid)
                .arg(COMMAND_ARG_CONTENT, item.getContent())
                .arg(COMMAND_ARG_PROJECT_ID, projectId)
                .arg(COMMAND_ARG_ITEM_DUE, due)
                .build();
        commands.add(addItemCommand);

        if (item.isCompleted()) {
            String completeCommandUuid = UUID.randomUUID().toString();
            TodoistCommand completeItemCommand = createCompleteItemCommand(completeCommandUuid, tmpUuid);
            commands.add(completeItemCommand);
        }

        if (item.isDeleted()) {
            String deleteCommandUuid = UUID.randomUUID().toString();
            TodoistCommand deleteItemCommand = createDeleteItemCommand(deleteCommandUuid, tmpUuid);
            commands.add(deleteItemCommand);
        }

        return commands;
    }

    private List<TodoistCommand> createUpdateItemCommand(ItemAmqpDto item, String commandUuid, long itemId) {
        List<TodoistCommand> commands = new LinkedList<>();

        String COMMAND_TYPE_ITEM_UPDATE = "item_update";
        String COMMAND_ARG_CONTENT = "content";
        String COMMAND_ARG_ITEM_ID = "id";
        String COMMAND_ARG_ITEM_DUE = "due";

        DueDto due = DueDto.builder()
                .isRecurring(false)
                .lang("en")
                .timezone("UTC")
                .date(item.getFinish().format(DateTimeFormatter.ISO_INSTANT))
                .build();


        TodoistCommand addItemCommand = TodoistCommand.builder()
                .type(COMMAND_TYPE_ITEM_UPDATE)
                .uuid(commandUuid)
                .arg(COMMAND_ARG_CONTENT, item.getContent())
                .arg(COMMAND_ARG_ITEM_ID, itemId)
                .arg(COMMAND_ARG_ITEM_DUE, due)
                .build();

        commands.add(addItemCommand);

        if (item.isCompleted()) {
            String completeCommandUuid = UUID.randomUUID().toString();
            TodoistCommand completeItemCommand = createCompleteItemCommand(completeCommandUuid, itemId);
            commands.add(completeItemCommand);
        }

        if (item.isDeleted()) {
            String deleteCommandUuid = UUID.randomUUID().toString();
            TodoistCommand deleteItemCommand = createDeleteItemCommand(deleteCommandUuid, itemId);
            commands.add(deleteItemCommand);
        }

        return commands;
    }

    private TodoistCommand createCompleteItemCommand(String commandUuid, long itemId) {
        String COMMAND_TYPE_ITEM_UPDATE = "item_complete";
        String COMMAND_ARG_ITEM_ID = "id";
        String COMMAND_ARG_DATE_COMPLETED = "date_completed";

        return TodoistCommand.builder()
                .type(COMMAND_TYPE_ITEM_UPDATE)
                .uuid(commandUuid)
                .arg(COMMAND_ARG_ITEM_ID, itemId)
                .arg(COMMAND_ARG_DATE_COMPLETED, ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT))
                .build();
    }

    private TodoistCommand createCompleteItemCommand(String commandUuid, String tmpItemId) {
        String COMMAND_TYPE_ITEM_UPDATE = "item_complete";
        String COMMAND_ARG_ITEM_ID = "id";
        String COMMAND_ARG_DATE_COMPLETED = "date_completed";

        return TodoistCommand.builder()
                .type(COMMAND_TYPE_ITEM_UPDATE)
                .uuid(commandUuid)
                .arg(COMMAND_ARG_ITEM_ID, tmpItemId)
                .arg(COMMAND_ARG_DATE_COMPLETED, ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT))
                .build();
    }

    private TodoistCommand createDeleteItemCommand(String commandUuid, long itemId) {
        String COMMAND_TYPE_ITEM_DELETE = "item_delete";
        String COMMAND_ARG_ITEM_ID = "id";

        return TodoistCommand.builder()
                .type(COMMAND_TYPE_ITEM_DELETE)
                .uuid(commandUuid)
                .arg(COMMAND_ARG_ITEM_ID, itemId)
                .build();
    }

    private TodoistCommand createDeleteItemCommand(String commandUuid, String tmpItemId) {
        String COMMAND_TYPE_ITEM_DELETE = "item_delete";
        String COMMAND_ARG_ITEM_ID = "id";

        return TodoistCommand.builder()
                .type(COMMAND_TYPE_ITEM_DELETE)
                .uuid(commandUuid)
                .arg(COMMAND_ARG_ITEM_ID, tmpItemId)
                .build();
    }

    private TodoistCommand createDeleteProjectCommand(String commandUuid, long projectId) {
        String COMMAND_TYPE_PROJECT_DELETE = "project_delete";
        String COMMAND_ARG_PROJECT_ID = "id";

        return TodoistCommand.builder()
                .type(COMMAND_TYPE_PROJECT_DELETE)
                .uuid(commandUuid)
                .arg(COMMAND_ARG_PROJECT_ID, projectId)
                .build();
    }

    private TodoistCommand createDeleteProjectCommand(String commandUuid, String tmpProjectId) {
        String COMMAND_TYPE_PROJECT_DELETE = "project_delete";
        String COMMAND_ARG_PROJECT_ID = "id";

        return TodoistCommand.builder()
                .type(COMMAND_TYPE_PROJECT_DELETE)
                .uuid(commandUuid)
                .arg(COMMAND_ARG_PROJECT_ID, tmpProjectId)
                .build();
    }
}
