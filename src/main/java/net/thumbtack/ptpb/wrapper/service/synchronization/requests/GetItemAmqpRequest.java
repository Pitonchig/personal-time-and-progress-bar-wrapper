package net.thumbtack.ptpb.wrapper.service.synchronization.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetItemAmqpRequest {
    private long userId;
    private long itemId;
}
