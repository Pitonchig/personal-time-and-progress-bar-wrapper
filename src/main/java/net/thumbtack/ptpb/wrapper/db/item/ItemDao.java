package net.thumbtack.ptpb.wrapper.db.item;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ItemDao {

    @NonNull
    private final ItemMapper itemMapper;

    public List<Item> getAllItems() {
        List<Item> items = new LinkedList<>();
        itemMapper.findAll().forEach(items::add);
        return items;
    }

    public Optional<Item> getItemById(long id) {
        return itemMapper.findById(id);
    }

    public void insertItem(Item item) {
        itemMapper.save(item);
    }

    public void insertItems(List<Item> items) {
        itemMapper.saveAll(items);
    }

    public void deleteAllItems() {
        itemMapper.deleteAll();
    }

    public List<Item> getItemsByProjectId(long id) {
        List<Item> items = new LinkedList<>();
        itemMapper.findByProjectId(id).forEach(items::add);
        return items;
    }

    public void deleteItemById(long itemId) {
        itemMapper.deleteById(itemId);
    }
}
