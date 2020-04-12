package net.thumbtack.ptpb.wrapper.client;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {
        "net.thumbtack.ptpb.wrapper.client"
})
public class TodoistClientConfiguration {

}
