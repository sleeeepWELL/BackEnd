package project.sleepwell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import project.sleepwell.config.AppProperties;

//@EnableConfigurationProperties(AppProperties.class)     //project 에서 사용할 수 있도록 선언
@EnableJpaAuditing
@SpringBootApplication
public class SleepwellApplication {

    public static void main(String[] args) {
        SpringApplication.run(SleepwellApplication.class, args);
    }

}
