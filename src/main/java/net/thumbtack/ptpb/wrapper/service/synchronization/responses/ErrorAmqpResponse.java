package net.thumbtack.ptpb.wrapper.service.synchronization.responses;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import net.thumbtack.ptpb.wrapper.service.synchronization.dto.ErrorAmqpDto;

import java.util.List;

@Data
@Builder
public class ErrorAmqpResponse {
    @Singular
    List<ErrorAmqpDto> errors;
}
