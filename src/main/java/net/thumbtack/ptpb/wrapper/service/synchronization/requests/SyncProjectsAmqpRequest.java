package net.thumbtack.ptpb.wrapper.service.synchronization.requests;

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
public class SyncProjectsAmqpRequest {
    private String userId;
    private boolean toTodoist;
    private boolean fromTodoist;
    private List<ProjectAmqpDto> projects;
}
