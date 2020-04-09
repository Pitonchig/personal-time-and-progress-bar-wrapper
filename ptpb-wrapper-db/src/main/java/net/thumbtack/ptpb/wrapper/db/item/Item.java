package net.thumbtack.ptpb.wrapper.db.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    private long id;
    private long userId;
    private long projectId;
    private String content;
    private long parentId;
    private boolean isChecked;
    private LocalDateTime dateAdded;
    private LocalDateTime dateCompleted;

//    private long legacy_id;
//    private long legacyProjectId;
//    private int priority;
//    private LocalDateTime due;
//    private long legacyParentId;
//    private int childOrder;
//    private long sectionId;
//    private int dayOrder;
//    private boolean isCollapsed;
//    private List<Integer> children;
//    private List<Integer> labels;
//    private long addedByUserId;
//    private long assignedByUserId;
//    private long responsibleUserId;
//    private boolean isInHistory;
//    private boolean isDeleted;
//    private long syncId;
}
