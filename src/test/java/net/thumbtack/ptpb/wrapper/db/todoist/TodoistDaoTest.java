package net.thumbtack.ptpb.wrapper.db.todoist;


import net.thumbtack.ptpb.wrapper.db.DbConfiguration;
import net.thumbtack.ptpb.wrapper.db.DbProperties;
import net.thumbtack.ptpb.wrapper.db.sync.SyncDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnableConfigurationProperties
@ContextConfiguration(classes = {
        DbConfiguration.class,
        DbProperties.class,
        TodoistDao.class
})
public class TodoistDaoTest {

    @Autowired
    private TodoistDao todoistDao;

    @BeforeEach
    void setup() {
        todoistDao.deleteAllTodoists();
    }

    @Test
    void testInsertTodoistAndGet() {
        String userId = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();

        Todoist todoist = Todoist.builder()
                .userId(userId)
                .token(token)
                .build();
        assertFalse(todoistDao.getTodoistByUserUuid(userId).isPresent());
        todoistDao.insertTodoist(todoist);

        Optional<Todoist> todoistOptional = todoistDao.getTodoistByUserUuid(userId);
        assertTrue(todoistOptional.isPresent());
        Todoist result = todoistOptional.get();
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(token, result.getToken());
    }

    @Test
    void testDelete() {
        String userId = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();

        Todoist todoist = Todoist.builder()
                .userId(userId)
                .token(token)
                .build();
        assertFalse(todoistDao.getTodoistByUserUuid(userId).isPresent());
        todoistDao.insertTodoist(todoist);
        assertTrue(todoistDao.getTodoistByUserUuid(userId).isPresent());
        todoistDao.delete(userId);
        assertFalse(todoistDao.getTodoistByUserUuid(userId).isPresent());
    }

    @Test
    void testIsTodoistLinked() {
        String userId = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString();

        Todoist todoist = Todoist.builder()
                .userId(userId)
                .token(token)
                .build();
        assertFalse(todoistDao.isTodoistLinked(userId));
        todoistDao.insertTodoist(todoist);
        assertTrue(todoistDao.isTodoistLinked(userId));
    }
}
