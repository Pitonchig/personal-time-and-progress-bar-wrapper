package net.thumbtack.ptpb.wrapper.client.syncdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TodoistResponse {
    @JsonProperty("sync_token")
    private String token;
    @JsonProperty("temp_id_mapping")
    private Map<String, Object> tempIdMapping;
    @JsonProperty("sync_status")
    private Map<String, String> syncStatus;
    @JsonProperty("full_sync")
    private boolean isFullSync;
}
