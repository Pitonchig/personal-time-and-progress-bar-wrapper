package net.thumbtack.ptpb.wrapper.service.synchronization;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import net.thumbtack.ptpb.wrapper.client.TodoistClientService;
import net.thumbtack.ptpb.wrapper.client.item.ItemDto;
import net.thumbtack.ptpb.wrapper.client.project.ProjectDto;
import net.thumbtack.ptpb.wrapper.client.syncdata.SyncResponse;
import net.thumbtack.ptpb.wrapper.client.syncdata.TodoistResourcesTypes;
import net.thumbtack.ptpb.wrapper.client.user.UserDto;
import net.thumbtack.ptpb.wrapper.db.item.ItemDao;
import net.thumbtack.ptpb.wrapper.db.project.ProjectDao;
import net.thumbtack.ptpb.wrapper.db.sync.Sync;
import net.thumbtack.ptpb.wrapper.db.sync.SyncDao;
import net.thumbtack.ptpb.wrapper.db.user.UserDao;
import net.thumbtack.ptpb.wrapper.service.synchronization.mappers.ItemMapper;
import net.thumbtack.ptpb.wrapper.service.synchronization.mappers.ProjectMapper;
import net.thumbtack.ptpb.wrapper.service.synchronization.mappers.UserMapper;
import net.thumbtack.ptpb.wrapper.service.synchronization.requests.SyncUserRequest;
import net.thumbtack.ptpb.wrapper.service.synchronization.responses.SyncUserResponse;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

import static net.thumbtack.ptpb.wrapper.client.syncdata.TodoistResourcesTypes.*;

@Component
@RequiredArgsConstructor
public class SynchronizationService {

    private final UserDao userDao;
    private final ProjectDao projectDao;
    private final ItemDao itemDao;
    private final SyncDao syncDao;

    private final TodoistClientService todoistClientService;

    private final String DEFAULT_TOKEN = "*";

    public SyncUserResponse syncUser(SyncUserRequest request) throws JsonProcessingException {
        List<TodoistResourcesTypes> resources = new LinkedList<>();
        resources.add(USER);
        resources.add(PROJECTS);
        resources.add(ITEMS);
        SyncResponse response = todoistClientService.getSyncData(request.getToken(), DEFAULT_TOKEN, resources);

        List<ItemDto> itemsDto = response.getItems();
        List<ProjectDto> projectsDto = response.getProjects();
        UserDto userDto = response.getUser();

        userDao.insertUser(UserMapper.toUser(userDto));
        projectDao.insertProjects(ProjectMapper.toProjects(projectsDto, userDto.getId()));
        itemDao.insertItems(ItemMapper.toItems(itemsDto));

        Sync sync = Sync.builder()
                .userId(userDto.getId())
                .syncToken(response.getToken())
                .build();
        syncDao.updateSync(sync);

        return SyncUserResponse.builder()
                .id(userDto.getId())
                .name(userDto.getFullName())
                .registered(userDto.getRegistered())
                .build();
    }

    private String getSyncTokenByUserId(long userId) {
        return syncDao.getSyncTokenByUserId(userId)
                .orElse(Sync.builder()
                        .syncToken(DEFAULT_TOKEN)
                        .build())
                .getSyncToken();
    }
}
