package net.thumbtack.ptpb.wrapper.db.sync;

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
public class Sync {
    @Id
    private String userId;
    private String syncToken;
}
