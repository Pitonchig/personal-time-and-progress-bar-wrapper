package net.thumbtack.ptpb.wrapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class PtpbWrapperService {
    public static void main(String[] args) {
        log.info("Personal time and progress bar wrapper service started.");
        SpringApplication.run(PtpbWrapperService.class, args);
    }
}
