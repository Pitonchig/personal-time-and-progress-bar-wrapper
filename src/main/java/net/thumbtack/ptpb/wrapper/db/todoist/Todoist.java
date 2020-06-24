package net.thumbtack.ptpb.wrapper.db.todoist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.persistence.Entity;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Todoist {
    @Id
    private String userId;
    private String token;
}
