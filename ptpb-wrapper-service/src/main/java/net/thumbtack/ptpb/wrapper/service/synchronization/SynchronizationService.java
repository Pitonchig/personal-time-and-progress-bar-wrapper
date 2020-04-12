package net.thumbtack.ptpb.wrapper.service.synchronization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.thumbtack.ptpb.wrapper.client.TodoistClientService;
import net.thumbtack.ptpb.wrapper.client.item.ItemDto;
import net.thumbtack.ptpb.wrapper.client.project.ProjectDto;
import net.thumbtack.ptpb.wrapper.client.syncdata.SyncResponse;
import net.thumbtack.ptpb.wrapper.client.syncdata.TodoistCommand;
import net.thumbtack.ptpb.wrapper.client.syncdata.TodoistResourcesTypes;
import net.thumbtack.ptpb.wrapper.client.syncdata.TodoistResponse;
import net.thumbtack.ptpb.wrapper.client.user.UserDto;
import net.thumbtack.ptpb.wrapper.db.item.ItemDao;
import net.thumbtack.ptpb.wrapper.db.project.ProjectDao;
import net.thumbtack.ptpb.wrapper.db.sync.Sync;
import net.thumbtack.ptpb.wrapper.db.sync.SyncDao;
import net.thumbtack.ptpb.wrapper.db.user.User;
import net.thumbtack.ptpb.wrapper.db.user.UserDao;
import net.thumbtack.ptpb.wrapper.service.synchronization.requests.*;
import net.thumbtack.ptpb.wrapper.service.synchronization.structmappers.ItemStructMapper;
import net.thumbtack.ptpb.wrapper.service.synchronization.structmappers.ProjectStructMapper;
import net.thumbtack.ptpb.wrapper.service.synchronization.structmappers.UserStructMapper;
import net.thumbtack.ptpb.wrapper.service.synchronization.responses.SyncUserAmqpResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static net.thumbtack.ptpb.wrapper.client.syncdata.TodoistResourcesTypes.*;
import static net.thumbtack.ptpb.wrapper.service.synchronization.ErrorCode.USER_NOT_FOUND;

@Component
//@RequiredArgsConstructor
public class SynchronizationService {

    private final ObjectMapper objectMapper;
    private final UserDao userDao;
    private final ProjectDao projectDao;
    private final ItemDao itemDao;
    private final SyncDao syncDao;

    private final TodoistClientService todoistClientService;

    private final String DEFAULT_TOKEN = "*";

    public SynchronizationService(ObjectMapper mapper, UserDao userDao,
                                  ProjectDao projectDao, ItemDao itemDao,
                                  SyncDao syncDao, TodoistClientService todoistClientService,
                                  RabbitMqMessageProvider provider) {
        this.objectMapper = mapper;
        this.userDao = userDao;
        this.projectDao = projectDao;
        this.itemDao = itemDao;
        this.syncDao = syncDao;
        this.todoistClientService = todoistClientService;

        provider.registerHandler(SyncUserAmqpRequest.class.getSimpleName(), this::syncUser);
        provider.registerHandler(CreateItemAmqpRequest.class.getSimpleName(), this::createItem);
        provider.registerHandler(DeleteItemAmqpRequest.class.getSimpleName(), this::deleteItem);
        provider.registerHandler(DeleteProjectAmqpRequest.class.getSimpleName(), this::deleteProject);
        provider.registerHandler(GetItemAmqpRequest.class.getSimpleName(), this::getItem);
        provider.registerHandler(GetItemsAmqpRequest.class.getSimpleName(), this::getItems);
        provider.registerHandler(GetProjectAmqpRequest.class.getSimpleName(), this::getProject);
        provider.registerHandler(GetProjectsAmqpRequest.class.getSimpleName(), this::getProjects);
    }

    public ResponseWrapper syncUser(String data) {
        try {
            SyncUserAmqpRequest request = objectMapper.readValue(data, SyncUserAmqpRequest.class);

            List<TodoistResourcesTypes> resources = new LinkedList<>();
            resources.add(USER);
            resources.add(PROJECTS);
            resources.add(ITEMS);
            SyncResponse response = todoistClientService.getSyncData(request.getToken(), DEFAULT_TOKEN, resources);

            List<ItemDto> itemsDto = response.getItems();
            List<ProjectDto> projectsDto = response.getProjects();
            UserDto userDto = response.getUser();

            userDao.insertUser(UserStructMapper.toUser(userDto));
            projectDao.insertProjects(ProjectStructMapper.toProjects(projectsDto, userDto.getId()));
            itemDao.insertItems(ItemStructMapper.toItems(itemsDto));

            Sync sync = Sync.builder()
                    .userId(userDto.getId())
                    .syncToken(response.getToken())
                    .build();
            syncDao.updateSync(sync);

            SyncUserAmqpResponse syncUserAmqpResponse = SyncUserAmqpResponse.builder()
                    .id(userDto.getId())
                    .name(userDto.getFullName())
                    .registered(userDto.getRegistered())
                    .build();

            return ResponseWrapper.builder()
                    .isOk(true)
                    .data(objectMapper.writeValueAsString(syncUserAmqpResponse))
                    .build();

        } catch (JsonProcessingException e) {
            return new ResponseWrapper(false, e.getMessage());
        }
    }

    public ResponseWrapper createItem(String data) {
        try {
            CreateItemAmqpRequest request = objectMapper.readValue(data, CreateItemAmqpRequest.class);
            Optional<User> userOptional = userDao.getUserById(request.getUserId());
            if (!userOptional.isPresent()) {
                return new ResponseWrapper(false, USER_NOT_FOUND.getMessage());
            }

            String uuid = userOptional.get().getToken();
            String commandUuid = UUID.randomUUID().toString();

            TodoistCommand todoistCommand = TodoistCommand.builder()
                    .type("item_add")
                    .tempId(UUID.randomUUID().toString())
                    .uuid(commandUuid)
                    .arg("content", request.getContent())
                    .arg("project_id", request.getProjectId())
                    .arg("priority", request.getPriority())
                    .build();

            TodoistResponse todoistResponse = todoistClientService.postData(uuid, todoistCommand);
            boolean isOk = isOkStatus(todoistResponse.getSyncStatus().get(commandUuid));
            return new ResponseWrapper(isOk, "");
        } catch (JsonProcessingException e) {
            return new ResponseWrapper(false, e.getMessage());
        }

    }

    public ResponseWrapper createProject(String data) {

        return null;
    }

    public ResponseWrapper deleteItem(String data) {
        return null;
    }

    public ResponseWrapper deleteProject(String data) {
        return null;
    }

    public ResponseWrapper getItem(String data) {
        return null;
    }

    public ResponseWrapper getItems(String data) {
        return null;
    }

    public ResponseWrapper getProject(String data) {
        return null;
    }

    public ResponseWrapper getProjects(String data) {
        return null;
    }

    private String getSyncTokenByUserId(long userId) {
        return syncDao.getSyncTokenByUserId(userId)
                .orElse(Sync.builder()
                        .syncToken(DEFAULT_TOKEN)
                        .build())
                .getSyncToken();
    }

    private boolean isOkStatus(String status) {
        return status.equals("ok");
    }

}
