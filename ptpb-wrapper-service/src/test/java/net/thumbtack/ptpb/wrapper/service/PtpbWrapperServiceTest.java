package net.thumbtack.ptpb.wrapper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.thumbtack.ptpb.wrapper.db.DbConfiguration;
import net.thumbtack.ptpb.wrapper.service.synchronization.ResponseWrapper;
import net.thumbtack.ptpb.wrapper.service.synchronization.SynchronizationService;
import net.thumbtack.ptpb.wrapper.service.synchronization.requests.SyncUserAmqpRequest;
import net.thumbtack.ptpb.wrapper.service.synchronization.responses.SyncUserAmqpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@EnableConfigurationProperties
@ComponentScan(basePackages = {
        "net.thumbtack.ptpb.wrapper"
})
@Import(DbConfiguration.class)
public class PtpbWrapperServiceTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private SynchronizationService synchronizationService;


    @Test
    void testSyncUser() throws JsonProcessingException {
        SyncUserAmqpRequest request = SyncUserAmqpRequest.builder()
                .token("5f6e430cf393ae5db86773b5e79989fbef6a28d9")
                .build();
        ResponseWrapper wrapper = synchronizationService.syncUser(mapper.writeValueAsString(request));
        assertTrue(wrapper.isOk());
        SyncUserAmqpResponse response = mapper.readValue(wrapper.getData(), SyncUserAmqpResponse.class);
        assertNotNull(response);
        assertTrue(response.getId() > 0);
        assertNotNull(response.getName());
    }

}