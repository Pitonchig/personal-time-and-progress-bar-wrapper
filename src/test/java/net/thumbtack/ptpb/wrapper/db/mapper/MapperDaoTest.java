package net.thumbtack.ptpb.wrapper.db.mapper;

import net.thumbtack.ptpb.wrapper.db.DbConfiguration;
import net.thumbtack.ptpb.wrapper.db.DbProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnableConfigurationProperties
@ContextConfiguration(classes = {
    DbConfiguration.class,
    DbProperties.class,
    ResourceDao.class
})
public class MapperDaoTest {

    @Autowired
    private ResourceDao resourceDao;

    @BeforeEach
    void setup() {
        resourceDao.deleteAllResources();
    }

    @Test
    void testInsertAndGetResourceById() {
        String uuid = UUID.randomUUID().toString();
        long todoistId = System.nanoTime();

        Resource resource = Resource.builder()
                .uuid(uuid)
                .todoistId(todoistId)
                .build();
        resourceDao.insertResource(resource);

        Optional<Resource> resultOptional = resourceDao.getResourceById(uuid);
        assertTrue(resultOptional.isPresent());

        Resource result = resultOptional.get();
        assertEquals(resource.getUuid(), result.getUuid());
        assertEquals(resource.getTodoistId(), result.getTodoistId());
    }

    @Test
    void testInsertAndGetResourceByTodoistId() {
        String uuid = UUID.randomUUID().toString();
        long todoistId = System.nanoTime();

        Resource resource = Resource.builder()
                .uuid(uuid)
                .todoistId(todoistId)
                .build();
        resourceDao.insertResource(resource);

        List<Resource> resultList = resourceDao.getResourcesByTodoistId(todoistId);
        assertFalse(resultList.isEmpty());
        assertEquals(1, resultList.size());

        Resource result = resultList.get(0);
        assertEquals(resource.getUuid(), result.getUuid());
        assertEquals(resource.getTodoistId(), result.getTodoistId());
    }

    @Test
    void testInsertResourcesList() {
        Resource resource1 = Resource.builder()
                .uuid(UUID.randomUUID().toString())
                .todoistId(System.nanoTime())
                .build();

        Resource resource2 = Resource.builder()
                .uuid(UUID.randomUUID().toString())
                .todoistId(System.nanoTime())
                .build();

        assertFalse(resourceDao.getResourceById(resource1.getUuid()).isPresent());
        assertFalse(resourceDao.getResourceById(resource2.getUuid()).isPresent());

        List<Resource> resourceList = Arrays.asList(resource1, resource2);
        resourceDao.insertResources(resourceList);

        assertTrue(resourceDao.getResourceById(resource1.getUuid()).isPresent());
        assertTrue(resourceDao.getResourceById(resource2.getUuid()).isPresent());
    }
}
