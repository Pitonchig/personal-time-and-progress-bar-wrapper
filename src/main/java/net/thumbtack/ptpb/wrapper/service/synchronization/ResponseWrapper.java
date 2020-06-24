package net.thumbtack.ptpb.wrapper.service.synchronization;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonSerialize
public class ResponseWrapper {
    private boolean isOk;
    private String data;
}
