//package project.sleepwell.kakaologin;
//
//import com.github.scribejava.core.oauth.OAuth20Service;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import project.sleepwell.config.MyConfigurationProperties;
//
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Slf4j
//@RequestMapping("/oauth")
//@RestController
//public class KakaoHandlerController {
//
//    @Autowired
//    MyConfigurationProperties myConfigurationProperties;
//
//    @GetMapping("/kakao")
//    public void redirectAuthorization(HttpServletResponse response) throws IOException {
//        //properties 에 넣어놓고, 시크릿으로 빼오는 코드 작성 할 것 (테스트 성공하면)
////        String baseUrl = myConfigurationProperties.getBaseUrl();
////        String clientId = "&client_id=" + myConfigurationProperties.getClientId();
////        String redirectUri = "&redirect_uri=" + myConfigurationProperties.getLoginUrl();
//
////        response.sendRedirect(baseUrl + clientId + redirectUri);
//        response.sendRedirect("https://kauth.kakao.com/oauth/authorize" +
//                "?client_id=3982623969b121595a2e4fff200e265e" +
//                "&redirect_uri=http://localhost:3000/oauth/callback/kakao" +
//                "&response_type=code");
//    }
//}
