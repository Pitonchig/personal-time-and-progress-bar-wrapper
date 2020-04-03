package net.thumbtack.ptpb.wrapper.client.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DueDto {
    private String date;
    private String timezone;
    private String string;
    private String lang;
    @JsonProperty("is_recurring")
    private boolean isRecurring;
}
