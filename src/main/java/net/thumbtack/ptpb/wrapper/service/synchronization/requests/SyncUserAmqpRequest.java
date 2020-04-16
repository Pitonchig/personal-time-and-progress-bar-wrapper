package net.thumbtack.ptpb.wrapper.service.synchronization.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncUserAmqpRequest implements Serializable {
    private String token;
}
