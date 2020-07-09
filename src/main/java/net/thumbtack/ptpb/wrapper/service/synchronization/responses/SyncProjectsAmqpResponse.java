package net.thumbtack.ptpb.wrapper.service.synchronization.responses;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.ptpb.wrapper.service.synchronization.dto.ProjectAmqpDto;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
public class SyncProjectsAmqpResponse {
    private String userId;
    private boolean toTodoist;
    private boolean fromTodoist;
    private List<ProjectAmqpDto> projects;
}
