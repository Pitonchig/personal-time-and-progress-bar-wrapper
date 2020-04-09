package net.thumbtack.ptpb.wrapper.client.item;

import net.thumbtack.ptpb.wrapper.client.TodoistClient;

public interface ItemClient extends TodoistClient {
    void addItem(ItemDto item);
}
