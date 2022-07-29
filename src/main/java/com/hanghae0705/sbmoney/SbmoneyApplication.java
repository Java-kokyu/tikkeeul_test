package com.hanghae0705.sbmoney;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
//@EnableConfigurationProperties(AppProperties.class)
public class SbmoneyApplication {
    public static void main(String[] args) {
        SpringApplication.run(SbmoneyApplication.class, args);
    }

}
