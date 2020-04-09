package net.thumbtack.ptpb.wrapper.db.item;


import lombok.RequiredArgsConstructor;
import net.thumbtack.ptpb.wrapper.db.DbConfiguration;
import net.thumbtack.ptpb.wrapper.db.DbProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@EnableConfigurationProperties
@ContextConfiguration(classes = {
        DbConfiguration.class,
        DbProperties.class,
        ItemDaoImpl.class
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemDaoTest {

    private final ItemDao itemDao;

    @BeforeEach
    void setup() {
        itemDao.deleteAllItems();
    }

    @Test
    void testInsertAndGetItemById() {
        long itemId = System.nanoTime();
        long projectId = System.nanoTime();
        long userId = System.nanoTime();

        Item item = Item.builder()
                .id(itemId)
                .projectId(projectId)
                .userId(userId)
                .content("test item")
                .build();
        itemDao.insertItem(item);

        Optional<Item> result = itemDao.getItemById(itemId);
        assertTrue(result.isPresent());
        assertEquals(item, result.get());
    }

    @Test
    void testGetAllItems() {
        List<Item> items = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            long itemId = System.nanoTime();
            long projectId = System.nanoTime();
            long userId = System.nanoTime();
            Item item = Item.builder()
                    .id(itemId)
                    .projectId(projectId)
                    .userId(userId)
                    .content(String.format("test item %d", i))
                    .build();
            items.add(item);
        }

        items.forEach(itemDao::insertItem);

        Item notInsertedItem = Item.builder()
                .id(System.nanoTime())
                .projectId(System.nanoTime())
                .userId(System.nanoTime())
                .content("not inserted test item")
                .build();

        List<Item> results = itemDao.getAllItems();
        assertAll(
                () -> assertTrue(items.containsAll(results)),
                () -> assertEquals(items.size(), results.size()),
                () -> assertFalse(results.contains(notInsertedItem))
        );
    }

    @Test
    void testDeleteAllItems() {
        int count = 10;
        assertEquals(0, itemDao.getAllItems().size());

        for (int i = 0; i < count; i++) {
            long itemId = System.nanoTime();
            long projectId = System.nanoTime();
            long userId = System.nanoTime();
            Item item = Item.builder()
                    .id(itemId)
                    .projectId(projectId)
                    .userId(userId)
                    .content(String.format("test item", i))
                    .build();
            itemDao.insertItem(item);
        }
        assertEquals(count, itemDao.getAllItems().size());
        itemDao.deleteAllItems();
        assertEquals(0, itemDao.getAllItems().size());
    }


    @Test
    void testGetItemsByProjectId() {
        List<Item> project1Items = new LinkedList<>();
        long project1Id = System.nanoTime();
        for (int i = 0; i < 10; i++) {
            long itemId = System.nanoTime();
            long userId = System.nanoTime();
            Item item = Item.builder()
                    .id(itemId)
                    .projectId(project1Id)
                    .userId(userId)
                    .content(String.format("test item %d", i))
                    .build();
            project1Items.add(item);
        }
        project1Items.forEach(itemDao::insertItem);

        List<Item> project2Items = new LinkedList<>();
        long project2Id = System.nanoTime();
        for (int i = 0; i < 7; i++) {
            long itemId = System.nanoTime();
            long userId = System.nanoTime();
            Item item = Item.builder()
                    .id(itemId)
                    .projectId(project2Id)
                    .userId(userId)
                    .content(String.format("test item %d", i))
                    .build();
            project2Items.add(item);
        }
        project2Items.forEach(itemDao::insertItem);

        List<Item> items = itemDao.getItemsByProjectId(project1Id);
        assertEquals(project1Items.size(), items.size());
        items.containsAll(project1Items);
    }
}
