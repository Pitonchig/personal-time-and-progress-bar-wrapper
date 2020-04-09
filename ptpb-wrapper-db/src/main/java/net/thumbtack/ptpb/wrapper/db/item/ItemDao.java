package net.thumbtack.ptpb.wrapper.db.item;

import java.util.List;
import java.util.Optional;

public interface ItemDao {
    List<Item> getAllItems();

    Optional<Item> getItemById(long id);

    void insertItem(Item item);

    void insertItems(List<Item> items);

    void deleteAllItems();

    List<Item> getItemsByProjectId(long id);

    void deleteItemById(long itemId);
}
