package net.thumbtack.ptpb.wrapper.db.user;

import net.thumbtack.ptpb.wrapper.db.item.Item;
import net.thumbtack.ptpb.wrapper.db.item.ItemDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//@EnableConfigurationProperties
//@ContextConfiguration(classes = {
//        DbConfiguration.class,
//        DbProperties.class,
//        ItemDao.class
//})
public class UserDaoTest {

    @Autowired
    private UserDao userDao;

    @Test
    void testInsertAndGetItemById() {
        String userId = "56d3cdff-52a7-4778-bdd6-738c2fe7b919";
        Optional<User> result = userDao.getUserById(userId);
        assertTrue(result.isPresent());
    }

}
