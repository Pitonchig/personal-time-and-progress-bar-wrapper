package net.thumbtack.ptpb.wrapper.db.user;

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
public class User {
    @Id
    private long id;
    private String name;
    private LocalDateTime registered;
    private String token;
}
