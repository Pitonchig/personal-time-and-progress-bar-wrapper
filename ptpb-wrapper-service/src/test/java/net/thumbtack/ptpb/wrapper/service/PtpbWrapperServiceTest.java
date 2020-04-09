package net.thumbtack.ptpb.wrapper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.thumbtack.ptpb.wrapper.service.synchronization.SynchronizationService;
import net.thumbtack.ptpb.wrapper.service.synchronization.requests.SyncUserRequest;
import net.thumbtack.ptpb.wrapper.service.synchronization.responses.SyncUserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@EnableConfigurationProperties
@ComponentScan(basePackages = {
        "net.thumbtack.ptpb.wrapper"
})
public class PtpbWrapperServiceTest {

    @Autowired
    private SynchronizationService synchronizationService;


    @Test
    void testSyncUser() throws JsonProcessingException {
        SyncUserRequest request = SyncUserRequest.builder()
                .token("5f6e430cf393ae5db86773b5e79989fbef6a28d9")
                .build();
        SyncUserResponse response = synchronizationService.syncUser(request);
        assertNotNull(response);
    }

}