package net.thumbtack.ptpb.wrapper.service.synchronization.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateItemAmqpRequest {
    private long userId;
    private long projectId;
    private String content;
    private int priority;
    private LocalDateTime due;
}
