package net.thumbtack.ptpb.wrapper.client.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import net.thumbtack.ptpb.wrapper.client.syncdata.TodoistClientImpl;
import net.thumbtack.ptpb.wrapper.client.TodoistClientConfiguration;
import net.thumbtack.ptpb.wrapper.client.TodoistClientProperties;
import net.thumbtack.ptpb.wrapper.client.syncdata.TodoistResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@EnableConfigurationProperties
@ContextConfiguration(classes = {
        TodoistClientImpl.class,
        ProjectClientImpl.class,
        TodoistClientConfiguration.class,
        TodoistClientProperties.class
})
public class ProjectClientTest {
    @Autowired
    private ProjectClient projectClient;

    @Test
    void testAddProjects() throws JsonProcessingException {
        String token = "5f6e430cf393ae5db86773b5e79989fbef6a28d9";
        String name = "test";
        int color = 37;
        long parentId = 0;
        int childOrder = 0;
        boolean isFavorite = false;

        TodoistResponse response = projectClient.addProject(token, name, color, parentId, childOrder, isFavorite);
        assertNotNull(response);
    }

}
