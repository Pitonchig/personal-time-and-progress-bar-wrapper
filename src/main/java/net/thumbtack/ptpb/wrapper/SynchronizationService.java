package net.thumbtack.ptpb.wrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.ptpb.wrapper.client.syncdata.*;
import net.thumbtack.ptpb.wrapper.db.mapper.Resource;
import net.thumbtack.ptpb.wrapper.db.mapper.ResourceDao;
import net.thumbtack.ptpb.wrapper.db.sync.Sync;
import net.thumbtack.ptpb.wrapper.db.sync.SyncDao;
import net.thumbtack.ptpb.wrapper.db.todoist.Todoist;
import net.thumbtack.ptpb.wrapper.db.todoist.TodoistDao;
import net.thumbtack.ptpb.wrapper.service.synchronization.RabbitMqMessageProvider;
import net.thumbtack.ptpb.wrapper.service.synchronization.ResponseWrapper;
import net.thumbtack.ptpb.wrapper.service.synchronization.dto.ItemAmqpDto;
import net.thumbtack.ptpb.wrapper.service.synchronization.dto.ProjectAmqpDto;
import net.thumbtack.ptpb.wrapper.service.synchronization.requests.*;
import net.thumbtack.ptpb.wrapper.service.synchronization.responses.SyncProjectsAmqpResponse;
import net.thumbtack.ptpb.wrapper.service.synchronization.responses.SyncUserTokenAmqpResponse;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static net.thumbtack.ptpb.wrapper.client.syncdata.TodoistResourcesTypes.*;

@Slf4j
@Component
public class SynchronizationService {

    private final ObjectMapper objectMapper;
    private final TodoistDao todoistDao;
    private final SyncDao syncDao;
    private final ResourceDao resourceDao;

    private final TodoistClientService todoistClientService;

    private final String DEFAULT_TOKEN = "*";

    public SynchronizationService(ObjectMapper mapper,
                                  TodoistDao todoistDao, SyncDao syncDao, ResourceDao resourceDao,
                                  TodoistClientService todoistClientService,
                                  RabbitMqMessageProvider provider) {
        this.objectMapper = mapper;
        this.todoistDao = todoistDao;
        this.syncDao = syncDao;
        this.resourceDao = resourceDao;
        this.todoistClientService = todoistClientService;

        provider.registerHandler(SyncUserTokenAmqpRequest.class.getSimpleName(), this::syncUserToken);
        provider.registerHandler(SyncProjectsAmqpRequest.class.getSimpleName(), this::syncProjects);
    }

    public ResponseWrapper syncUserToken(String data) {
        try {
            SyncUserTokenAmqpRequest request = objectMapper.readValue(data, SyncUserTokenAmqpRequest.class);

            List<TodoistResourcesTypes> resources = new LinkedList<>();
            resources.add(USER);
            SyncResponse response = todoistClientService.getSyncData(request.getToken(), DEFAULT_TOKEN, resources);
            if (response == null) {
                return new ResponseWrapper(false, "TODOIST_TIMEOUT");    //TODO: error code list
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
            syncDao.updateSyncs(Collections.singletonList(sync));

            SyncUserTokenAmqpResponse syncUserTokenAmqpResponse = SyncUserTokenAmqpResponse.builder()
                    .isValid(isTokenValid)
                    .build();

            return ResponseWrapper.builder()
                    .isOk(true)
                    .data(objectMapper.writeValueAsString(syncUserTokenAmqpResponse))
                    .build();

        } catch (JsonProcessingException e) {
            return new ResponseWrapper(false, e.getMessage());
        }
    }

    public ResponseWrapper syncProjects(String data) {
        try {
            SyncProjectsAmqpRequest request = objectMapper.readValue(data, SyncProjectsAmqpRequest.class);

            Optional<Todoist> todoist = todoistDao.getTodoistByUserUuid(request.getUserId());
            if (todoist.isEmpty()) {
                return new ResponseWrapper(false, "TODOIST_TOKEN_NOT_FOUND");   //TODO: error code list
            }

            String token = todoist.get().getToken();
            List<TodoistCommand> commands = new LinkedList<>();
            Map<String, String> uuidMapper = new HashMap<>(); //<tmpUui, ptpbUuid>;

            for (ProjectAmqpDto project : request.getProjects()) {
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

            List<String> errors = todoistResponse.getSyncStatus().values().stream().filter(p -> !isOkStatus(p)).collect(Collectors.toList());
            if (!errors.isEmpty()) {
                return new ResponseWrapper(false, errors.toString());
            }

            saveResourcesMap(todoistResponse.getTempIdMapping(), uuidMapper);

            SyncProjectsAmqpResponse syncProjectsAmqpResponse = SyncProjectsAmqpResponse.builder()
                    .userId(request.getUserId())
                    .fromTodoist(request.isFromTodoist())
                    .toTodoist(request.isToTodoist())
                    .projects(request.getProjects())
                    .build();

            return ResponseWrapper.builder()
                    .isOk(true)
                    .data(objectMapper.writeValueAsString(syncProjectsAmqpResponse))
                    .build();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ResponseWrapper(false, e.getMessage());
        }
    }

    private void saveResourcesMap(Map<String, Long> tempIdMapping, Map<String, String> uuidMapper) {
        for (String key : tempIdMapping.keySet()) {
            Long todoistId = tempIdMapping.get(key);
            String ptpbUuid = uuidMapper.get(key);
            Resource resource = new Resource(ptpbUuid, todoistId);
            resourceDao.insertResource(resource);
        }
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
            commands.add(createAddItemCommand(uuidMapper, item, itemCommandUuid, projectTempId));
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
                commands.add(createUpdateItemCommand(item, itemCommandUuid, itemResource.get().getTodoistId()));
            } else {
                commands.add(createAddItemCommand(uuidMapper, item, itemCommandUuid, projectId));
            }
        }
        return commands;

    }

    private TodoistCommand createAddItemCommand(Map<String, String> uuidMapper, ItemAmqpDto item, String commandUuid, String projectTempUuid) {
        String COMMAND_TYPE_ITEM_ADD = "item_add";
        String COMMAND_ARG_CONTENT = "content";
        String COMMAND_ARG_PROJECT_ID = "project_id";

        String tmpUuid = UUID.randomUUID().toString();
        uuidMapper.put(tmpUuid, item.getId());
        return TodoistCommand.builder()
                .type(COMMAND_TYPE_ITEM_ADD)
                .tempId(tmpUuid)
                .uuid(commandUuid)
                .arg(COMMAND_ARG_CONTENT, item.getContent())
                .arg(COMMAND_ARG_PROJECT_ID, projectTempUuid)
                .build();
    }

    private TodoistCommand createAddItemCommand(Map<String, String> uuidMapper, ItemAmqpDto item, String commandUuid, long projectId) {
        String COMMAND_TYPE_ITEM_ADD = "item_add";
        String COMMAND_ARG_CONTENT = "content";
        String COMMAND_ARG_PROJECT_ID = "project_id";

        String tmpUuid = UUID.randomUUID().toString();
        uuidMapper.put(tmpUuid, item.getId());
        return TodoistCommand.builder()
                .type(COMMAND_TYPE_ITEM_ADD)
                .tempId(tmpUuid)
                .uuid(commandUuid)
                .arg(COMMAND_ARG_CONTENT, item.getContent())
                .arg(COMMAND_ARG_PROJECT_ID, projectId)
                .build();
    }

    private TodoistCommand createUpdateItemCommand(ItemAmqpDto item, String commandUuid, long itemId) {
        String COMMAND_TYPE_ITEM_UPDATE = "item_update";
        String COMMAND_ARG_CONTENT = "content";
        String COMMAND_ARG_ITEM_ID = "id";

        return TodoistCommand.builder()
                .type(COMMAND_TYPE_ITEM_UPDATE)
                .uuid(commandUuid)
                .arg(COMMAND_ARG_CONTENT, item.getContent())
                .arg(COMMAND_ARG_ITEM_ID, itemId)
                .build();
    }


//    public ResponseWrapper syncProjects(String data) {
//        try {
//            SyncProjectsAmqpRequest request = objectMapper.readValue(data, SyncProjectsAmqpRequest.class);
//            String token = "token";
//
//            List<TodoistResourcesTypes> resources = new LinkedList<>();
//            resources.add(PROJECTS);
//            resources.add(ITEMS);
//            SyncResponse response = todoistClientService.getSyncData(token, DEFAULT_TOKEN, resources);
//
//
//            Sync sync = Sync.builder()
//                    .userId(request.getUserId())
//                    .syncToken(response.getToken())
//                    .build();
//            syncDao.updateSyncs(Collections.singletonList(sync));
//
//            SyncProjectsAmqpResponse syncProjectsAmqpResponse = SyncProjectsAmqpResponse.builder()
//                    .build();
//
//            return ResponseWrapper.builder()
//                    .isOk(true)
//                    .data(objectMapper.writeValueAsString(syncProjectsAmqpResponse))
//                    .build();
//
//        } catch (JsonProcessingException e) {
//            return new ResponseWrapper(false, e.getMessage());
//        }
//    }


//    public ResponseWrapper syncUser(String data) {
//        try {
//            SyncUserAmqpRequest request = objectMapper.readValue(data, SyncUserAmqpRequest.class);
//
//            List<TodoistResourcesTypes> resources = new LinkedList<>();
//            resources.add(USER);
//            resources.add(PROJECTS);
//            resources.add(ITEMS);
//            SyncResponse response = todoistClientService.getSyncData(request.getToken(), DEFAULT_TOKEN, resources);
//
//            List<ItemDto> itemsDto = response.getItems();
//            List<ProjectDto> projectsDto = response.getProjects();
//            UserDto userDto = response.getUser();
//
//            User user = UserStructMapper.toUser(userDto);
//            user.setToken(request.getToken());
//            userDao.insertUsers(Collections.singletonList(user));
//            projectDao.insertProjects(ProjectStructMapper.toProjects(projectsDto, userDto.getId()));
//            itemDao.insertItems(ItemStructMapper.toItems(itemsDto));
//
//            Sync sync = Sync.builder()
//                    .userId(userDto.getId())
//                    .syncToken(response.getToken())
//                    .build();
//            syncDao.updateSyncs(Collections.singletonList(sync));
//
//            SyncUserAmqpResponse syncUserAmqpResponse = SyncUserAmqpResponse.builder()
//                    .id(request.getUserId())
//                    .name(userDto.getFullName())
//                    .registered(userDto.getRegistered())
//                    .build();
//
//            return ResponseWrapper.builder()
//                    .isOk(true)
//                    .data(objectMapper.writeValueAsString(syncUserAmqpResponse))
//                    .build();
//
//        } catch (JsonProcessingException e) {
//            return new ResponseWrapper(false, e.getMessage());
//        }
//    }

//
//    public ResponseWrapper createItem(String data) {
//        try {
//            CreateItemAmqpRequest request = objectMapper.readValue(data, CreateItemAmqpRequest.class);
//            Optional<User> userOptional = userDao.getUserById(request.getUserId());
//            if (userOptional.isEmpty()) {
//                return new ResponseWrapper(false, USER_NOT_FOUND.getMessage());
//            }
//
//            String uuid = userOptional.get().getToken();
//            String commandUuid = UUID.randomUUID().toString();
//
//            TodoistCommand todoistCommand = TodoistCommand.builder()
//                    .type("item_add")
//                    .tempId(UUID.randomUUID().toString())
//                    .uuid(commandUuid)
//                    .arg("content", request.getContent())
//                    .arg("project_id", request.getProjectId())
//                    .arg("priority", request.getPriority())
//                    .build();
//
//            TodoistResponse todoistResponse = todoistClientService.postData(uuid, todoistCommand);
//            boolean isOk = isOkStatus(todoistResponse.getSyncStatus().get(commandUuid));
//            if (isOk) {
//                syncUsersData(userOptional.get());
//            }
//            return new ResponseWrapper(isOk, "");
//        } catch (JsonProcessingException e) {
//            return new ResponseWrapper(false, e.getMessage());
//        }
//
//    }
//
//    public ResponseWrapper createProject(String data) {
//        try {
//            CreateProjectAmqpRequest request = objectMapper.readValue(data, CreateProjectAmqpRequest.class);
//            Optional<User> userOptional = userDao.getUserById(request.getUserId());
//            if (userOptional.isEmpty()) {
//                return new ResponseWrapper(false, USER_NOT_FOUND.getMessage());
//            }
//
//            String uuid = userOptional.get().getToken();
//            String commandUuid = UUID.randomUUID().toString();
//
//            TodoistCommand todoistCommand = TodoistCommand.builder()
//                    .type("project_add")
//                    .tempId(UUID.randomUUID().toString())
//                    .uuid(commandUuid)
//                    .arg("name", request.getName())
//                    .arg("color", request.getColor())
//                    .arg("is_favorite", request.isFavorite())
//                    .build();
//
//            TodoistResponse todoistResponse = todoistClientService.postData(uuid, todoistCommand);
//            boolean isOk = isOkStatus(todoistResponse.getSyncStatus().get(commandUuid));
//            if (isOk) {
//                syncUsersData(userOptional.get());
//            }
//            return new ResponseWrapper(isOk, "");
//        } catch (JsonProcessingException e) {
//            return new ResponseWrapper(false, e.getMessage());
//        }
//    }
//
//    public ResponseWrapper deleteItem(String data) {
//        try {
//            DeleteItemAmqpRequest request = objectMapper.readValue(data, DeleteItemAmqpRequest.class);
//            Optional<User> userOptional = userDao.getUserById(request.getUserId());
//            if (userOptional.isEmpty()) {
//                return new ResponseWrapper(false, USER_NOT_FOUND.getMessage());
//            }
//
//            String uuid = userOptional.get().getToken();
//            String commandUuid = UUID.randomUUID().toString();
//
//            TodoistCommand todoistCommand = TodoistCommand.builder()
//                    .type("item_delete")
//                    .tempId(UUID.randomUUID().toString())
//                    .uuid(commandUuid)
//                    .arg("name", request.getItemId())
//                    .build();
//
//            TodoistResponse todoistResponse = todoistClientService.postData(uuid, todoistCommand);
//            itemDao.deleteItemById(request.getItemId());
//            boolean isOk = isOkStatus(todoistResponse.getSyncStatus().get(commandUuid));
//            return new ResponseWrapper(isOk, "");
//        } catch (JsonProcessingException e) {
//            return new ResponseWrapper(false, e.getMessage());
//        }
//    }
//
//    public ResponseWrapper deleteProject(String data) {
//        try {
//            DeleteProjectAmqpRequest request = objectMapper.readValue(data, DeleteProjectAmqpRequest.class);
//            Optional<User> userOptional = userDao.getUserById(request.getUserId());
//            if (userOptional.isEmpty()) {
//                return new ResponseWrapper(false, USER_NOT_FOUND.getMessage());
//            }
//
//            String uuid = userOptional.get().getToken();
//            String commandUuid = UUID.randomUUID().toString();
//
//            TodoistCommand todoistCommand = TodoistCommand.builder()
//                    .type("project_delete")
//                    .tempId(UUID.randomUUID().toString())
//                    .uuid(commandUuid)
//                    .arg("name", request.getProjectId())
//                    .build();
//
//            TodoistResponse todoistResponse = todoistClientService.postData(uuid, todoistCommand);
//            projectDao.deleteProjectById(request.getProjectId());
//            boolean isOk = isOkStatus(todoistResponse.getSyncStatus().get(commandUuid));
//            return new ResponseWrapper(isOk, "");
//        } catch (JsonProcessingException e) {
//            return new ResponseWrapper(false, e.getMessage());
//        }
//    }
//
//    public ResponseWrapper getItem(String data) {
//        try {
//            GetItemAmqpRequest request = objectMapper.readValue(data, GetItemAmqpRequest.class);
//            Optional<User> userOptional = userDao.getUserById(request.getUserId());
//            if (userOptional.isEmpty()) {
//                return new ResponseWrapper(false, USER_NOT_FOUND.getMessage());
//            }
//
//            Optional<Item> itemResponse = itemDao.getItemById(request.getItemId());
//            if (itemResponse.isEmpty()) {
//                return new ResponseWrapper(false, ITEM_NOT_FOUND.getMessage());
//            }
//            GetItemAmqpResponse amqpResponse = itemToGetItemAmqpResponse(itemResponse.get());
//
//            return new ResponseWrapper(true, objectMapper.writeValueAsString(amqpResponse));
//        } catch (JsonProcessingException e) {
//            return new ResponseWrapper(false, e.getMessage());
//        }
//    }
//
//    public ResponseWrapper getItems(String data) {
//        try {
//            GetItemsAmqpRequest request = objectMapper.readValue(data, GetItemsAmqpRequest.class);
//            Optional<User> userOptional = userDao.getUserById(request.getUserId());
//            if (userOptional.isEmpty()) {
//                return new ResponseWrapper(false, USER_NOT_FOUND.getMessage());
//            }
//            List<GetItemAmqpResponse> amqpResponses = getItemAmqpResponseListByProjectId(request.getProjectId());
//            return new ResponseWrapper(true, objectMapper.writeValueAsString(amqpResponses));
//        } catch (JsonProcessingException e) {
//            return new ResponseWrapper(false, e.getMessage());
//        }
//    }
//
//    public ResponseWrapper getProject(String data) {
//        try {
//            GetProjectAmqpRequest request = objectMapper.readValue(data, GetProjectAmqpRequest.class);
//            Optional<User> userOptional = userDao.getUserById(request.getUserId());
//            if (userOptional.isEmpty()) {
//                return new ResponseWrapper(false, USER_NOT_FOUND.getMessage());
//            }
//
//            Optional<Project> projectOptional = projectDao.getProjectById(request.getProjectId());
//            if (projectOptional.isEmpty()) {
//                return new ResponseWrapper(false, PROJECT_NOT_FOUND.getMessage());
//            }
//            Project project = projectOptional.get();
//
//            GetProjectAmqpResponse amqpResponse = GetProjectAmqpResponse.builder()
//                    .id(project.getId())
//                    .name(project.getName())
//                    .color(project.getColor())
//                    .items(getItemAmqpResponseListByProjectId(request.getProjectId()))
//                    .build();
//            return new ResponseWrapper(true, objectMapper.writeValueAsString(amqpResponse));
//        } catch (JsonProcessingException e) {
//            return new ResponseWrapper(false, e.getMessage());
//        }
//    }
//
//    public ResponseWrapper getProjects(String data) {
//        try {
//            GetProjectsAmqpRequest request = objectMapper.readValue(data, GetProjectsAmqpRequest.class);
//            Optional<User> userOptional = userDao.getUserById(request.getUserId());
//            if (userOptional.isEmpty()) {
//                return new ResponseWrapper(false, USER_NOT_FOUND.getMessage());
//            }
//            List<Project> projects = projectDao.getProjectsByUserId(request.getUserId());
//            List<GetProjectAmqpResponse> amqpResponses = new LinkedList<>();
//            for (Project project : projects) {
//                GetProjectAmqpResponse amqpResponse = GetProjectAmqpResponse.builder()
//                        .id(project.getId())
//                        .name(project.getName())
//                        .color(project.getColor())
//                        .items(getItemAmqpResponseListByProjectId(project.getId()))
//                        .build();
//                amqpResponses.add(amqpResponse);
//            }
//            return new ResponseWrapper(true, objectMapper.writeValueAsString(amqpResponses));
//        } catch (JsonProcessingException e) {
//            return new ResponseWrapper(false, e.getMessage());
//        }
//    }

    //    private String getSyncTokenByUserId(long userId) {
//        return syncDao.getSyncTokenByUserId(userId)
//                .orElse(Sync.builder()
//                        .syncToken(DEFAULT_TOKEN)
//                        .build())
//                .getSyncToken();
//    }
//
    private boolean isOkStatus(String status) {
        return status.equals("ok");
    }

//    private List<GetItemAmqpResponse> getItemAmqpResponseListByProjectId(long projectId) {
//        List<Item> items = itemDao.getItemsByProjectId(projectId);
//        List<GetItemAmqpResponse> amqpResponses = new LinkedList<>();
//        items.forEach(p -> amqpResponses.add(itemToGetItemAmqpResponse(p)));
//        return amqpResponses;
//    }

//    private GetItemAmqpResponse itemToGetItemAmqpResponse(Item item) {
//        return GetItemAmqpResponse.builder()
//                .id(item.getId())
//                .userId(item.getUserId())
//                .projectId(item.getProjectId())
//                .isCompleted(item.getDateCompleted() != null)
//                .content(item.getContent())
//                .build();
//    }

//    private void syncUsersData(User user) throws JsonProcessingException {
//        List<TodoistResourcesTypes> resources = new LinkedList<>();
//        resources.add(USER);
//        resources.add(PROJECTS);
//        resources.add(ITEMS);
//        SyncResponse response = todoistClientService.getSyncData(user.getToken(), getSyncTokenByUserId(user.getId()), resources);
//
//        List<ItemDto> itemsDto = response.getItems();
//        List<ProjectDto> projectsDto = response.getProjects();
//        UserDto userDto = response.getUser();
//
//        userDao.insertUser(UserStructMapper.toUser(userDto));
//        projectDao.insertProjects(ProjectStructMapper.toProjects(projectsDto, userDto.getId()));
//        itemDao.insertItems(ItemStructMapper.toItems(itemsDto));
//
//        Sync sync = Sync.builder()
//                .userId(userDto.getId())
//                .syncToken(response.getToken())
//                .build();
//        syncDao.updateSync(sync);
//    }

}
