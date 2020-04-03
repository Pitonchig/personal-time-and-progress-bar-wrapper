package net.thumbtack.ptpb.wrapper.client.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    private Long id;
    @JsonProperty("legacy_id")
    private Long legacyId;
    private String name;
    private Integer color;
    @JsonProperty("parent_id")
    private Long parentId;
    @JsonProperty("legacy_parent_id")
    private Long legacyParentId;
    @JsonProperty("child_order")
    private Integer childOrder;
    @JsonProperty("collapsed")
    private boolean isCollapsed;
    @JsonProperty("shared")
    private boolean isShared;
    @JsonProperty("is_deleted")
    private boolean isDeleted;
    @JsonProperty("is_archived")
    private boolean isArchived;
    @JsonProperty("is_favorite")
    private boolean isFavorite;
    @JsonProperty("inbox_project")
    private boolean isInboxProject;
    @JsonProperty("team_project")
    private boolean isTeamProject;
}
