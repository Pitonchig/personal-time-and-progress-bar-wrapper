package net.thumbtack.ptpb.wrapper.rabbitmq.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.thumbtack.ptpb.wrapper.client.TodoistResourcesTypes;
import net.thumbtack.ptpb.wrapper.client.dto.UserDto;
import net.thumbtack.ptpb.wrapper.client.dto.response.SyncResponse;
import net.thumbtack.ptpb.wrapper.client.services.TodoistClientService;
import net.thumbtack.ptpb.wrapper.common.PtpbException;
import net.thumbtack.ptpb.wrapper.db.mapper.ResourceDao;
import net.thumbtack.ptpb.wrapper.db.sync.SyncDao;
import net.thumbtack.ptpb.wrapper.db.todoist.TodoistDao;
import net.thumbtack.ptpb.wrapper.rabbitmq.dto.requests.SyncUserTokenAmqpRequest;
import net.thumbtack.ptpb.wrapper.rabbitmq.dto.responses.SyncUserTokenAmqpResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static net.thumbtack.ptpb.wrapper.client.TodoistResourcesTypes.*;
import static net.thumbtack.ptpb.wrapper.common.ErrorCode.TODOIST_SERVICE_TIMEOUT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class SynchronizationServiceTest {

    @MockBean
    private TodoistDao todoistDao;
    @MockBean
    private SyncDao syncDao;
    @MockBean
    private ResourceDao resourceDao;
    @MockBean
    private TodoistClientService todoistClientService;

    private final String DEFAULT_TOKEN = "*";

    @Test
    void testSyncNotLinkedUser() throws JsonProcessingException {
        SynchronizationService synchronizationService = new SynchronizationService(todoistDao, syncDao, resourceDao, todoistClientService);
        List<TodoistResourcesTypes> resources = Arrays.asList(PROJECTS, ITEMS, USER);
        String token = UUID.randomUUID().toString();
        String uiserId = UUID.randomUUID().toString();

        SyncUserTokenAmqpRequest request = SyncUserTokenAmqpRequest.builder()
                .token(token)
                .userId(uiserId)
                .build();
        when(todoistClientService.getSyncData(request.getToken(), DEFAULT_TOKEN, resources)).thenReturn(null);

        try {
            synchronizationService.syncUserToken(request);
            fail();
        } catch (PtpbException e) {
            assertEquals(1, e.getErrors().size());
            assertTrue(e.getErrors().contains(TODOIST_SERVICE_TIMEOUT));
        }
    }

    @Test
    void testSyncUserToken() throws JsonProcessingException, PtpbException {
        SynchronizationService synchronizationService = new SynchronizationService(todoistDao, syncDao, resourceDao, todoistClientService);
        String token = UUID.randomUUID().toString();
        String uiserId = UUID.randomUUID().toString();

        SyncUserTokenAmqpRequest request = SyncUserTokenAmqpRequest.builder()
                .token(token)
                .userId(uiserId)
                .build();

        UserDto user = UserDto.builder()
                .id(System.nanoTime())
                .build();
        SyncResponse syncUserResponse = SyncResponse.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .build();

        when(todoistClientService.getSyncData(any(), any(), any())).thenReturn(syncUserResponse);
        SyncUserTokenAmqpResponse response = synchronizationService.syncUserToken(request);
        assertNotNull(response);
        assertTrue(response.isValid());
    }

    @Test
    void testSyncUserWithWrongToken() throws JsonProcessingException, PtpbException {
        SynchronizationService synchronizationService = new SynchronizationService(todoistDao, syncDao, resourceDao, todoistClientService);

        String token = UUID.randomUUID().toString();
        String uiserId = UUID.randomUUID().toString();

        SyncUserTokenAmqpRequest request = SyncUserTokenAmqpRequest.builder()
                .token(token)
                .userId(uiserId)
                .build();

        SyncResponse syncUserResponse = SyncResponse.builder()
                .token(UUID.randomUUID().toString())
                .user(null)
                .build();

        when(todoistClientService.getSyncData(any(), any(), any())).thenReturn(syncUserResponse);
        SyncUserTokenAmqpResponse response = synchronizationService.syncUserToken(request);
        assertNotNull(response);
        assertFalse(response.isValid());
    }

}
