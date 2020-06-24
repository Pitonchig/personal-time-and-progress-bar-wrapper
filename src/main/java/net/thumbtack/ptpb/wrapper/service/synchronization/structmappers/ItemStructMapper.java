package net.thumbtack.ptpb.wrapper.service.synchronization.structmappers;

import net.thumbtack.ptpb.wrapper.client.item.ItemDto;
import net.thumbtack.ptpb.wrapper.db.item.Item;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
//
//public class ItemStructMapper {
//
//    static public Item toItem(ItemDto dto) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
//        LocalDateTime addedDateTime = (dto.getDateAdded() != null) ? LocalDateTime.parse(dto.getDateAdded(), formatter) : null;
//        LocalDateTime completedDateTime = (dto.getDateCompleted() != null) ? LocalDateTime.parse(dto.getDateCompleted(), formatter) : null;
//
//        return Item.builder()
//                .id(dto.getId())
//                .userId(dto.getUserId())
//                .projectId(dto.getProjectId())
//                .content(dto.getContent())
//                .parentId(dto.getParentId())
//                .dateAdded(addedDateTime)
//                .dateCompleted(completedDateTime)
//                .isChecked(dto.isChecked())
//                .build();
//    }
//
//    static public List<Item> toItems(List<ItemDto> itemDtoList) {
//        List<Item> list = new LinkedList<>();
//        itemDtoList.forEach(p -> list.add(toItem(p)));
//        return list;
//    }
//
//
//}
