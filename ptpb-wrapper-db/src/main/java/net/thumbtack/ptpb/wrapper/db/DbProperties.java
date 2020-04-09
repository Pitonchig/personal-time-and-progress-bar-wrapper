package net.thumbtack.ptpb.wrapper.db;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("db")
@NoArgsConstructor
public class DbProperties {
    private String host;
    private String namespace;
    private int port;
}
