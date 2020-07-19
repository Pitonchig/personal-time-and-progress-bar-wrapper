package net.thumbtack.ptpb.wrapper.rabbitmq.dto.responses;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
public class SyncUserTokenAmqpResponse {
    private boolean isValid;
}
