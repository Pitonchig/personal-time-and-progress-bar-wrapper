package net.thumbtack.ptpb.wrapper.client.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private long id;
    @JsonProperty("legacy_id")
    private long legacy_id;
    @JsonProperty("user_id")
    private long userId;
    @JsonProperty("project_id")
    private long projectId;
    @JsonProperty("legacy_project_id")
    private long legacyProjectId;
    private String content;
    private int priority;
    private DueDto due;
    @JsonProperty("parent_id")
    private long parentId;
    @JsonProperty("legacy_parent_id")
    private long legacyParentId;
    @JsonProperty("child_order")
    private int childOrder;
    @JsonProperty("section_id")
    private long sectionId;
    @JsonProperty("day_order")
    private int dayOrder;
    @JsonProperty("collapsed")
    private boolean isCollapsed;
    private List<ItemDto> children;
    private List<Integer> labels;
    @JsonProperty("added_by_uid")
    private long addedByUserId;
    @JsonProperty("assigned_by_uid")
    private long assignedByUserId;
    @JsonProperty("responsible_uid")
    private long responsibleUserId;
    @JsonProperty("checked")
    private boolean isChecked;
    @JsonProperty("in_history")
    private boolean isInHisotry;
    @JsonProperty("is_deleted")
    private boolean isDeleted;
    @JsonProperty("sync_id")
    private long syncId;
    @JsonProperty("date_added")
    private String dateAdded;
    @JsonProperty("date_completed")
    private String dateCompleted;
}
