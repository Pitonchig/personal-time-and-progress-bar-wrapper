package net.thumbtack.ptpb.wrapper.service.synchronization.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private String id;
    private String projectId;
    private String userId;
    private String content;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime start;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime finish;
    @JsonProperty("isCompleted")
    private boolean isCompleted;
}
