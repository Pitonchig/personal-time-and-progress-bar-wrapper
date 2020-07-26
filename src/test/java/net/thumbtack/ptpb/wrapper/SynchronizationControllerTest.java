package net.thumbtack.ptpb.wrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.thumbtack.ptpb.wrapper.common.PtpbException;
import net.thumbtack.ptpb.wrapper.rabbitmq.RabbitMqMessageProvider;
import net.thumbtack.ptpb.wrapper.rabbitmq.dto.ResponseWrapper;
import net.thumbtack.ptpb.wrapper.rabbitmq.dto.requests.SyncProjectsAmqpRequest;
import net.thumbtack.ptpb.wrapper.rabbitmq.dto.requests.SyncUserTokenAmqpRequest;
import net.thumbtack.ptpb.wrapper.rabbitmq.dto.responses.SyncProjectsAmqpResponse;
import net.thumbtack.ptpb.wrapper.rabbitmq.dto.responses.SyncUserTokenAmqpResponse;
import net.thumbtack.ptpb.wrapper.rabbitmq.services.SynchronizationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        ObjectMapper.class
})
public class SynchronizationControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private SynchronizationService synchronizationService;
    @MockBean
    private RabbitMqMessageProvider rabbitMqMessageProvider;

    @Test
    void testSyncUserToken() throws JsonProcessingException, PtpbException {
        SynchronizationController controller = new SynchronizationController(objectMapper, synchronizationService, rabbitMqMessageProvider);
        SyncUserTokenAmqpResponse response = SyncUserTokenAmqpResponse.builder()
                .isValid(true)
                .build();
        SyncUserTokenAmqpRequest request = new SyncUserTokenAmqpRequest(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        when(synchronizationService.syncUserToken(any())).thenReturn(response);
        ResponseWrapper responseWrapper = controller.syncUserToken( objectMapper.writeValueAsString(request));
        assertNotNull(responseWrapper);

        assertTrue(responseWrapper.isOk());
        SyncUserTokenAmqpResponse result = objectMapper.readValue(responseWrapper.getData(), SyncUserTokenAmqpResponse.class);
        assertNotNull(result);
        assertEquals(response.isValid(), result.isValid());
        verify(synchronizationService, times(1)).syncUserToken(any());
    }

    @Test
    void testSyncProjects() throws JsonProcessingException, PtpbException {
        SynchronizationController controller = new SynchronizationController(objectMapper, synchronizationService, rabbitMqMessageProvider);
        SyncProjectsAmqpRequest request = SyncProjectsAmqpRequest.builder()
                .toTodoist(true)
                .fromTodoist(true)
                .userId(UUID.randomUUID().toString())
                .build();
        SyncProjectsAmqpResponse response = SyncProjectsAmqpResponse.builder()
                .userId(request.getUserId())
                .toTodoist(request.isToTodoist())
                .fromTodoist(request.isFromTodoist())
                .build();

        when(synchronizationService.syncProjects(any())).thenReturn(response);
        String json = objectMapper.writeValueAsString(request);
        ResponseWrapper responseWrapper = controller.syncProjects(json);
        assertNotNull(responseWrapper);
        verify(synchronizationService, times(1)).syncProjects(any());
    }

}
