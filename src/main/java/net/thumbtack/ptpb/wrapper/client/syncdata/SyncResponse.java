package net.thumbtack.ptpb.wrapper.client.syncdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.SuperBuilder;
import net.thumbtack.ptpb.wrapper.client.item.ItemDto;
import net.thumbtack.ptpb.wrapper.client.project.ProjectDto;
import net.thumbtack.ptpb.wrapper.client.user.UserDto;

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
