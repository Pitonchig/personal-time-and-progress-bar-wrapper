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
}
