package net.thumbtack.ptpb.wrapper.service.synchronization.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
public class ItemAmqpDto {
    private String id;
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "UTC")
    private LocalDateTime start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "UTC")
    private LocalDateTime finish;
    @JsonProperty("isCompleted")
    private boolean isCompleted;
}
