package project.sleepwell.kakaologin;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import project.sleepwell.config.MyConfigurationProperties;

/**
 * authorize_code 로 카카오 서버에 access token 받아올 메서드
 * 방법 1. HTTP header, body 이용
 */
@Slf4j
@Component
public class KakaoOAuth2 {

    @Autowired
    MyConfigurationProperties myConfigurationProperties;

    //인가 코드로 토큰 요청 -> 사용자 정보 요청
    public KakaoUserInfo getUserInfo(String code) {
        log.info("프론트에서 받은 코드(getUserInfo 메서드) = {}", code);

        //1.인가코드 -> 액세스 토큰
        String accessToken = getAccessToken(code);
        //2.액세스 토큰 -> 카카오 사용자 정보
        KakaoUserInfo userInfo = getUserInfoByToken(accessToken);
        return userInfo;
    }


    //프론트에게 받은 인가 코드로 카카오 서버한테 요청해서 액세스 토큰 받기
    public String requestAccessToken(String authorizedCode) {
        String accessToken = getAccessToken(authorizedCode);
        return accessToken;
    }


    //방법 2 -> 프론트에서 백으로 코드를 넘겨줌
    private String getAccessToken(String code) {
        log.info("프론트에서 받은 코드(getAccessToken 메서드) = {}", code);
        //HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//        headers.add("Content-type", "application/json;charset=utf-8");

        //HttpBody 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", myConfigurationProperties.getClientId());
        params.add("redirect_uri", "http://localhost:3000/oauth/callback/kakao");
//        params.add("redirect_uri", "http://sleepwell.com.s3-website.ap-northeast-2.amazonaws.com/oauth/callback/kakao");
        params.add("code", code);
        params.add("client_secret", myConfigurationProperties.getClientSecret());

        log.info("HttpBody 오브젝트 생성 후 ={}", code);
        log.info("client id 불러온 것: {}", myConfigurationProperties.getClientId());
//        log.info("client secret 불러온 것: {}", myConfigurationProperties.getClientSecret());


        //HttpHeader 와 HttpBody 를 하나의 오브젝트에 담기
        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        //Http 요청하기 (Post 방식), response 변수의 응답 받음
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        log.info("카카오에 토큰 요청, response = {}", response);

        /**
         *요청 성공 시, 응답은 JSON 객체로 Redirect URI 에 전달되며 두 가지 종류의
         * 토큰 값과 타입, 초 단위로 된 만료 시간을 포함하고 있다. (우리는 access_token 만 필요)
         */
        //Json -> 액세스 토큰 파싱
        String tokenJson = response.getBody();
        JSONObject rjson = new JSONObject(tokenJson);
        String accessToken = rjson.getString("access_token");

        System.out.println("액세스 토큰 제발!!!" + accessToken);

        return accessToken;
    }

    private KakaoUserInfo getUserInfoByToken(String token) {
        //HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //HttpHeader 와 HttpBody 를 하나의 오브젝트에 담기
        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        //Http 요청하기 (Post 방식), response 변수의 응답 받음
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        JSONObject body = new JSONObject(response.getBody());
        Long id = body.getLong("id");
        String email = body.getJSONObject("kakao_account").getString("email");
        String nickname = body.getJSONObject("properties").getString("nickname");

        return new KakaoUserInfo(id, email, nickname);

    }

}
