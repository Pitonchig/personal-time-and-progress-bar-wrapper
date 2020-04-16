package net.thumbtack.ptpb.wrapper.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import net.thumbtack.ptpb.wrapper.client.syncdata.SyncResponse;
import net.thumbtack.ptpb.wrapper.client.syncdata.TodoistClientService;
import net.thumbtack.ptpb.wrapper.client.syncdata.TodoistResourcesTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.thumbtack.ptpb.wrapper.client.syncdata.TodoistResourcesTypes.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class TodoistClientServiceTest {
    private String token = "5f6e430cf393ae5db86773b5e79989fbef6a28d9";

    @Autowired
    private TodoistClientService todoistClient;

    @Test
    void testGetProjects() throws JsonProcessingException {
        String syncToken = "*";
        List<TodoistResourcesTypes> resources = Collections.singletonList(PROJECTS);
        SyncResponse result = todoistClient.getSyncData(token, syncToken, resources);
        assertNotNull(result);
    }

    @Test
    void testGetItems() throws JsonProcessingException {
        String syncToken = "*";
        List<TodoistResourcesTypes> resources = Collections.singletonList(ITEMS);
        SyncResponse result = todoistClient.getSyncData(token, syncToken, resources);
        assertNotNull(result);
    }

    @Test
    void testGetAllSyncData() throws JsonProcessingException {
        String syncToken = "*";
        List<TodoistResourcesTypes> resources = Arrays.asList(PROJECTS, ITEMS, USER);
        SyncResponse result = todoistClient.getSyncData(token, syncToken, resources);
        assertNotNull(result);
    }

}
