package net.thumbtack.ptpb.wrapper.db.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.persistence.Entity;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Project {
    @Id
    private long id;
    private long userId;
    private String name;
    private Integer color;

//    private Long legacyId;
//    private Long parentId;
//    private Long legacyParentId;
//    private Integer childOrder;
//    private boolean isCollapsed;
//    private boolean isShared;
//    private boolean isDeleted;
//    private boolean isArchived;
//    private boolean isFavorite;
//    private boolean isInboxProject;
//    private boolean isTeamProject;
}
