package net.thumbtack.ptpb.wrapper.service.synchronization.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
public class ProjectAmqpDto {
    private String id;
    private String name;
    @Singular
    List<ItemAmqpDto> items;
    private boolean isDeleted;
}
