package net.thumbtack.ptpb.wrapper.rabbitmq.dto.responses;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import net.thumbtack.ptpb.wrapper.rabbitmq.dto.ErrorAmqpDto;

import java.util.List;

@Data
@Builder
public class ErrorAmqpResponse {
    @Singular
    List<ErrorAmqpDto> errors;
}
