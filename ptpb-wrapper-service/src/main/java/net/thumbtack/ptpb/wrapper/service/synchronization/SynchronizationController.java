package net.thumbtack.ptpb.wrapper.service.synchronization;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import net.thumbtack.ptpb.wrapper.service.synchronization.requests.SyncUserRequest;
import net.thumbtack.ptpb.wrapper.service.synchronization.responses.SyncUserResponse;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Component
@RabbitListener(queues = "ptpb-wrapper")
@RequiredArgsConstructor
public class SynchronizationController {

    private final SynchronizationService synchronizationService;

    @RabbitHandler
    public SyncUserResponse getUser(SyncUserRequest request) throws JsonProcessingException {
        return synchronizationService.syncUser(request);
    }

}
