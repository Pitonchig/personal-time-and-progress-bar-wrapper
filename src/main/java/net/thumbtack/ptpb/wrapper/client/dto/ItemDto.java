package net.thumbtack.ptpb.wrapper.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemDto {
    private long id;
    @JsonProperty("user_id")
    private long userId;
    @JsonProperty("project_id")
    private long projectId;
    private String content;
    @JsonProperty("parent_id")
    private long parentId;
    @JsonProperty("checked")
    private boolean isChecked;
    @JsonProperty("date_added")
    private String dateAdded;
    @JsonProperty("date_completed")
    private String dateCompleted;
    @JsonProperty("is_deleted")
    private boolean isDeleted;
    private DueDto due;
}
