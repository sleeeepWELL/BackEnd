package project.sleepwell.kakaologin;

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
@Component
public class KakaoOAuth2 {

    @Autowired
    MyConfigurationProperties myConfigurationProperties;

//    public KakaoUserInfo getUserInfo(String authorizedCode) {
//        //1.인가코드 -> 액세스 토큰
//        String accessToken = getAccessToken(authorizedCode);
//
//        //2.액세스 토큰 -> 카카오 사용자 정보
//        KakaoUserInfo userInfo = getUserInfoByToken(accessToken);
//
//        return userInfo;
//    }

    //토큰으로 카카오 유저 정보 가져오기
    public KakaoUserInfo getUserInfo(String token) {
        //2.액세스 토큰 -> 카카오 사용자 정보
        KakaoUserInfo userInfo = getUserInfoByToken(token);

        return userInfo;
    }

    //방법 2 -> 프론트에서 처리하면 서버에서는 필요없는 메서드
    private String getAccessToken(String authorizedCode) {
        //HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //HttpBody 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
//        params.add("client_id", "");        //==클라이언트 아이디===============//
        params.add("client_id", myConfigurationProperties.getClientId());
        params.add("redirect_url", "http://localhost:3000/kakaoLogin");
//        params.add("redirect_url", "http://54.180.79.156/kakaoLogin");
        params.add("code", authorizedCode);

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

        //Json -> 액세스 토큰 파싱
        String tokenJson = response.getBody();
        JSONObject rjson = new JSONObject(tokenJson);
        String accessToken = rjson.getString("access_token");

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
