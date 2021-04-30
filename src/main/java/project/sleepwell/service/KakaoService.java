package project.sleepwell.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


//인증 코드로 access token 받아올 메서드 작성

/**
 * https://kauth.kakao.com/oauth/token 경로로 필수 요구 파라미터를 POST 방식으로 요청해라.
 */
@Slf4j
@Service
public class KakaoService {     //KakaoAPI

    public String getAccessToken(String authorize_code) {
        //테스트 후 카멜로 바꾸기
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try{
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //post 요청을 위해 기본값이 false 인 setDoOutput 을 true 로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //post 요청 시 필요한 요구조건들을 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=클라이언트 아이디");
            sb.append("&redirect_uri=http://localhost:8080/kakaoLogin");
            sb.append("&code=" + authorize_code);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이 나오면 성공
            int responseCode = conn.getResponseCode();
            log.info("responseCode= {}", responseCode);

            //요청을 통해 얻은 JSON 타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            log.info("response body = {}", result);

            //Gson 라이브러리에 포함된 클래스로 JSON 파싱 객체 생성하기
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            log.info("access_token = {}", access_Token);
            log.info("refresh_token = {}", refresh_Token);

            br.close();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return access_Token;
    }

    public HashMap<String, Object> getUserInfo(String access_Token) {
        //요청하는 클라이언트마다 가진 정보가 다를 수 있으므로 HashMap 선언
        HashMap<String, Object> userInfo = new HashMap<>();
        String reqURL = "https://kapi.kakao.com/v2/user/me";
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            //사용자 전체정보를 얻고 싶다면 아래와 같이 요청
            conn.setRequestProperty("Authorization", "Bearer" + access_Token);
            int responseCode = conn.getResponseCode();
            log.info("responseCode = {}", responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            log.info("response body = {}", result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
            JsonObject kakao_account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

            String nickname = properties.getAsJsonObject().get("nickname").getAsString();
            String email = kakao_account.getAsJsonObject().get("email").getAsString();

            userInfo.put("nickname", nickname);
            userInfo.put("email", email);


        } catch (IOException e) {
            e.printStackTrace();
        }

        return userInfo;

    }
}
