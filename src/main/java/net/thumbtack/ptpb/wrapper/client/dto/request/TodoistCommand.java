package net.thumbtack.ptpb.wrapper.client.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TodoistCommand {
    private String type;
    @JsonProperty("temp_id")
    private String tempId;
    private String uuid;
    @Singular
    private Map<String, Object> args;
}
