package net.thumbtack.ptpb.wrapper.service.synchronization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ResponseWrapper {
    private boolean isOk;
    private String data;
}
