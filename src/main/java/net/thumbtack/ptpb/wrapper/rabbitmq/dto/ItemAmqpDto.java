package net.thumbtack.ptpb.wrapper.rabbitmq.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
public class ItemAmqpDto {
    private String id;
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "UTC")
    private ZonedDateTime start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "UTC")
    private ZonedDateTime finish;
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "UTC")
    private ZonedDateTime completion;
    @JsonProperty("isCompleted")
    private boolean isCompleted;
    @JsonProperty("isDeleted")
    private boolean isDeleted;
}
