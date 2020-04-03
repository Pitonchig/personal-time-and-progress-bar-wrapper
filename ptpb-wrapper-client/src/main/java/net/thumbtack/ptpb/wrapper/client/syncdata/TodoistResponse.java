package net.thumbtack.ptpb.wrapper.client.syncdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.ptpb.wrapper.client.item.ItemDto;
import net.thumbtack.ptpb.wrapper.client.project.ProjectDto;


import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoistResponse {
    @JsonProperty("sync_token")
    private String token;
    @JsonProperty("temp_id_mapping")
    private TempIdMapping tempIdMapping;
    @JsonProperty("full_sync")
    private boolean isFullSync;
    List<ItemDto> items;
    List<ProjectDto> projects;
}
