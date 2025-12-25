package dev.wilsonblanco.reportgenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReportgeneratorApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportgeneratorApplication.class);
    public static void main(String[] args) {
        LOGGER.info("Iniciando aplicación Report Generator...");
        SpringApplication.run(ReportgeneratorApplication.class, args);
    }

}
