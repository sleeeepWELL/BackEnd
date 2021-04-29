package project.sleepwell.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final long MAX_AGE_SECS = 3600;     //seconds 단위

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")  //CORS 를 적용할 URL 패턴
                .allowedOrigins("*")         //자원 공유를 허락할 Origin 지정. "http://localhost:3000(리액트 ip)"
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")    //요청 허용하는 헤더
                .allowCredentials(true) //쿠키 허용
                .maxAge(MAX_AGE_SECS);
    }
}
