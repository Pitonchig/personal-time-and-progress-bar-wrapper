package net.thumbtack.ptpb.wrapper.db;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("db")
@NoArgsConstructor
public class DbProperties {
    private String host;
    private String namespace;
    private int port;
}
