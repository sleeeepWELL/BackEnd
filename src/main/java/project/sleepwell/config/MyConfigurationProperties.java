package project.sleepwell.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@Getter @Setter
@ConfigurationProperties(prefix = "my-config")
public class MyConfigurationProperties {

    private String clientId;
    private String adminToken;
    private String secretKey;

    private String baseUrl;
    private String loginUrl;
    private String clientSecret;


}
