package net.thumbtack.ptpb.wrapper.service;

import lombok.extern.slf4j.Slf4j;
import net.thumbtack.ptpb.wrapper.client.TodoistClientConfiguration;
import net.thumbtack.ptpb.wrapper.db.DbConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Slf4j
@ComponentScan(basePackages = {"net.thumbtack.ptpb.wrapper.service"})
@Import({
        TodoistClientConfiguration.class,
        DbConfiguration.class,
        PtpbWrapperServiceConfiguration.class,
})
@Configuration
@SpringBootApplication
public class PtpbWrapperService {

    public static void main(String[] args) {
        log.info("Personal time and progress bar wrapper service started.");
        SpringApplication.run(PtpbWrapperService.class, args);
    }
}
