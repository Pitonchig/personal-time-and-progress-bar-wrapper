package net.thumbtack.ptpb.wrapper.client.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.SuperBuilder;
import net.thumbtack.ptpb.wrapper.client.dto.ItemDto;
import net.thumbtack.ptpb.wrapper.client.dto.ProjectDto;
import net.thumbtack.ptpb.wrapper.client.dto.UserDto;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SyncResponse extends TodoistResponse {
    @Singular
    private List<ItemDto> items;
    @Singular
    private List<ProjectDto> projects;
    private UserDto user;

}
