package project.sleepwell.kakaologin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.sleepwell.config.MyConfigurationProperties;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


//서버 혼자 테스트하는 용도 입니다.
@Slf4j
@RequestMapping("/oauth")
@RestController
public class KakaoHandlerController {

    @Autowired
    MyConfigurationProperties myConfigurationProperties;

    @GetMapping("/kakao")
    public void redirectAuthorization(HttpServletResponse response) throws IOException {
        //properties 에 넣어놓고, 시크릿으로 빼오는 코드 작성 할 것 (테스트 성공하면)
        String baseUrl = myConfigurationProperties.getBaseUrl();
        String clientId = "&client_id=" + myConfigurationProperties.getClientId();
        String redirectUri = "&redirect_uri=" + myConfigurationProperties.getLoginUrl();

        response.sendRedirect(baseUrl + clientId + redirectUri);
    }
}
