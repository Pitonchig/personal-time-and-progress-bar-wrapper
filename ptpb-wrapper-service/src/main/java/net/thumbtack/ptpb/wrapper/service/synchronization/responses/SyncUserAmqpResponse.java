package net.thumbtack.ptpb.wrapper.service.synchronization.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncUserAmqpResponse {
    private long id;
    private String name;
    private String registered;
}
