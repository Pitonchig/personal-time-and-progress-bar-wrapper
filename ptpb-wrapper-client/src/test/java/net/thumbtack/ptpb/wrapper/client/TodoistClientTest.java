package net.thumbtack.ptpb.wrapper.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import net.thumbtack.ptpb.wrapper.client.syncdata.TodoistResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
@EnableConfigurationProperties
@ContextConfiguration(classes = {
        TodoistClient.class,
        TodoistClientConfiguration.class,
        TodoistClientProperties.class
})
public class TodoistClientTest {
    private String token = "5f6e430cf393ae5db86773b5e79989fbef6a28d9";

    @Autowired
    private TodoistClient todoistClient;

    @Test
    void testGetProjects() throws JsonProcessingException {
        String syncToken = "*";
        List<String> resources = Collections.singletonList("projects");
        TodoistResponse result = todoistClient.getSyncData(token, syncToken, resources);
        assertNotNull(result);
    }

    @Test
    void testGetItems() throws JsonProcessingException {
        log.info("token={}", token);
        String syncToken = "*";
        List<String> resources = Collections.singletonList("items");
        TodoistResponse result = todoistClient.getSyncData(token, syncToken, resources);
        assertNotNull(result);
    }

    @Test
    void testGetAllSyncData() throws JsonProcessingException {
        String syncToken = "*";
        List<String> resources = Arrays.asList("projects", "items");
        TodoistResponse result = todoistClient.getSyncData(token, syncToken, resources);
        assertNotNull(result);
    }

}
