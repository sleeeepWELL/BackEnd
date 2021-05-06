package project.sleepwell.kakaologin;

import com.github.scribejava.core.oauth.OAuth20Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequestMapping("/oauth")
@RestController
public class KakaoHandlerController {

    @GetMapping("/kakao")
    public void redirectAuthorization(HttpServletResponse response) throws IOException {
        response.sendRedirect("");

    }

//    @GetMapping("/callback/kakao")
//    public void redirectAuthorization(@PathVariable String provider, HttpServletResponse response) throws IOException {
//
//        response.sendRedirect(authorizationUrl);
//    }
}
