package net.thumbtack.ptpb.wrapper.client;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("todoist")
@NoArgsConstructor
public class TodoistClientProperties {
    private String uri;
}
