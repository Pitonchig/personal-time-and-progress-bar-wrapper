package net.thumbtack.ptpb.wrapper.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = {
        "net.thumbtack.ptpb.wrapper"
})
public class PtpbWrapperService {

    public static void main(String[] args) {
        log.info("Personal time and progress bar wrapper service started.");
        SpringApplication.run(PtpbWrapperService.class, args);
    }

}