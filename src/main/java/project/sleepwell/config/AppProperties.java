package project.sleepwell.config;


import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.ArrayList;
import java.util.List;


@Getter
@ConfigurationProperties(prefix = "app")
public class AppProperties {        //binding 하기

    private final OAuth oauth = new OAuth();

    @Getter
    public static final class OAuth {
        private String tokenSecret;
        private long tokenExpirationMsec;
    }

    @Getter
    public static final class OAuth2 {
        private List<String> authorizedRedirectUris = new ArrayList<>();
    }


}
