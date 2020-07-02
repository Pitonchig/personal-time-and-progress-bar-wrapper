package net.thumbtack.ptpb.wrapper.service.synchronization.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectAmqpDto {
    private String id;
    private String name;
    @Singular
    List<ItemAmqpDto> items;
}
