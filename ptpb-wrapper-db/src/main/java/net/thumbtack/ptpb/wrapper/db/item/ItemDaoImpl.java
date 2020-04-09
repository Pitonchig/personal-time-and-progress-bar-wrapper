package net.thumbtack.ptpb.wrapper.db.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ItemDaoImpl implements ItemDao {

    private final ItemMapper itemMapper;

    @Override
    public List<Item> getAllItems() {
        List<Item> items = new LinkedList<>();
        itemMapper.findAll().forEach(items::add);
        return items;
    }

    @Override
    public Optional<Item> getItemById(long id) {
        return itemMapper.findById(id);
    }

    @Override
    public void insertItem(Item item) {
        itemMapper.save(item);
    }

    @Override
    public void insertItems(List<Item> items) {
        itemMapper.saveAll(items);
    }

    @Override
    public void deleteAllItems() {
        itemMapper.deleteAll();
    }

    @Override
    public List<Item> getItemsByProjectId(long id) {
        List<Item> items = new LinkedList<>();
        itemMapper.findByProjectId(id).forEach(items::add);
        return items;
    }

    @Override
    public void deleteItemById(long itemId) {
        itemMapper.deleteById(itemId);
    }
}
