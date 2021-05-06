package project.sleepwell.kakaologin;

import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import project.sleepwell.domain.user.StatusEnum;
import project.sleepwell.service.UserService;
import project.sleepwell.web.dto.LoginDto;
import project.sleepwell.web.dto.TokenDto;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;

@RestController
@RequiredArgsConstructor
public class KakaoController {

    private final KakaoOAuth2 kakaoOAuth2;
    private final KakaoService kakaoService;

    @GetMapping("/kakaoLogin")
    public String kakaoLogin(String code, HttpServletResponse response) throws IOException {

        LoginDto kakaoLoginDto = kakaoService.kakaoLogin(code);

        //kakaoLoginDto = email, password 매핑
        if (kakaoLoginDto != null) {

            String email = kakaoLoginDto.getEmail();
            String password = kakaoLoginDto.getPassword();

//            HttpPost 요청
//            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpClient client = HttpClientBuilder.create().build();
//            HttpClient client = HttpClientBuilder.create().build();
//       /data, url 로 보냄
//            String postUrl = "http://54.180.79.156/api/login";
            String postUrl = "http://localhost:8080/api/login";
            HttpPost httpPost = new HttpPost(postUrl);
            String data = "{" +
                    "\"email\": \"" + email + "\", " +
                    "\"password\": \"" + password + "\"" +
                    "}";

////            StringEntity entity = new StringEntity(data, ContentType.APPLICATION_FORM_URLENCODED);
            StringEntity entity = new StringEntity(data, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            System.out.println(entity.getContent());
//
//           //응답 헤더에 있는 값들 다 받은 것. -> 네트워크 or 포스트맨으로 확인
            HttpResponse responsePost = client.execute(httpPost);
            System.out.println("responsePost = " + responsePost);


            //======//
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("Content-type", "application/json;charset=utf-8");
//
//            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//            params.add("email", email);
//            params.add("password", password);
//
//            RestTemplate rt = new RestTemplate();
//            HttpEntity<MultiValueMap<String, String>> kakaoRequest= new HttpEntity<>(params, headers);
//
//            ResponseEntity<String> responseEntity = rt.exchange(
//                    "http://localhost:8080/api/login",
//                    HttpMethod.POST,
//                    kakaoRequest,
//                    String.class
//            );




            //요청 상태 코드 200 ok 라면
            if (responsePost.getStatusLine().getStatusCode() == 200) {  //여기에 들어오지도 않네
                //응답 바디에서 tokenDto 꺼내기
                HttpEntity entity1 = responsePost.getEntity();
                //{"grantType":"bearer","accessToken":"","refreshToken":"","accessTokenExpiresIn":} 형태로 출력하기
                String content = EntityUtils.toString(entity1);
                System.out.println(content);
                return content;
                //제발 되게 해주세요. 제발요.......

                //respnose body 에서 값 꺼내기 - 유저 정보 꺼내는 것 (필요없음)
//                HttpEntity httpEntity = responsePost.getEntity();
//                String message = EntityUtils.toString(httpEntity);
//                System.out.println("httpEntity = " + httpEntity);
//                System.out.println("message = " + EntityUtils.toString(httpEntity));

                //-->content type json 으로 바꾸니까 여기까지 들어오는데, accessToken 에서 null 터진다.

                //response header 에서 token 꺼내기
//                String accessToken = responsePost.getFirstHeader("Authorization").getValue();
//                responsePost.getFirstHeader("Authorization").getValue();
//
//                //원래 프론트에서 login 후 header 에 accessToken 을 담아서 요청을 하니까
//                response.addHeader("Authorization", accessToken);
//                response.getWriter().write(message);

//                String tokenDto = responseEntity.getBody();
//                JSONObject rjson = new JSONObject(tokenDto);
//                String grantType = rjson.getString("grantType");
//                String accessToken = rjson.getString("accessToken");
//                String refreshToken = rjson.getString("refreshToken");
//                long accessTokenExpiresIn = rjson.getLong("accessTokenExpiresIn");
//
//                TokenDto tokenDtoBuild = TokenDto.builder()
//                        .grantType(grantType)
//                        .accessToken(accessToken)
//                        .refreshToken(refreshToken)
//                        .accessTokenExpiresIn(accessTokenExpiresIn)
//                        .build();
//                return tokenDtoBuild;


            } else {
                //로그인 실패
                response.getWriter().write("kakao login fail");
            }

        } else {
            // kakaoLoginDto == null 인 경우
            response.getWriter().write("There is not that kakao user.");


        }
        return null;
    }
}



