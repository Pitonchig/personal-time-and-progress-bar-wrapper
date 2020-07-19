package net.thumbtack.ptpb.wrapper.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectDto {
    private Long id;
    private String name;
    private Integer color;
    @JsonProperty("is_deleted")
    private boolean isDeleted;
}
