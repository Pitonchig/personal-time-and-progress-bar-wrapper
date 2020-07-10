package net.thumbtack.ptpb.wrapper.client.item;

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

//    @JsonProperty("legacy_id")
//    private long legacy_id;
//    @JsonProperty("legacy_project_id")
//    private long legacyProjectId;
//    private int priority;
//    private DueDto due;
//    @JsonProperty("legacy_parent_id")
//    private long legacyParentId;
//    @JsonProperty("child_order")
//    private int childOrder;
//    @JsonProperty("section_id")
//    private long sectionId;
//    @JsonProperty("day_order")
//    private int dayOrder;
//    @JsonProperty("collapsed")
//    private boolean isCollapsed;
//    private List<ItemDto> children;
//    private List<Integer> labels;
//    @JsonProperty("added_by_uid")
//    private long addedByUserId;
//    @JsonProperty("assigned_by_uid")
//    private long assignedByUserId;
//    @JsonProperty("responsible_uid")
//    private long responsibleUserId;
//    @JsonProperty("in_history")
//    private boolean isInHistory;
//    @JsonProperty("is_deleted")
//    private boolean isDeleted;
//    @JsonProperty("sync_id")
//    private long syncId;
}
