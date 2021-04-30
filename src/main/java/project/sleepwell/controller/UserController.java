package project.sleepwell.controller;

import com.nimbusds.oauth2.sdk.TokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.sleepwell.dto.*;
import project.sleepwell.repository.UserRepository;
import project.sleepwell.service.KakaoService;
import project.sleepwell.service.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final KakaoService kakaoService;


    @GetMapping("/test")
    public String test() {
        return "test: success.";
    }

    @GetMapping("/only/user")
    public String testUser() {
        return "권한 있는 사람만 보이는 메세지";
    }

    //email, username, password
    @PostMapping("/signup")
    public Long createUser(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        return userService.createUser(signupRequestDto);
    }

    //login
    @PostMapping("/api/login")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(userService.login(loginDto));  //token 발행
    }

    //callback 받는 URI
    /**
     * 카카오 Authorization Server 는 이 uri 에 code 값을 넘겨줄 것이다.
     */
    @RequestMapping("/kakaoLogin")  //get mapping
    public String kakaoLogin(@RequestParam String code) {

        String access_Token = kakaoService.getAccessToken(code);
        System.out.println("######" + code);

        HashMap<String, Object> userInfo = kakaoService.getUserInfo(access_Token);
        System.out.println("login Controller = " + userInfo);

        //세션은 안쓸 거니까 날리고.
        //클라이언트의 이메일이 존재할 때 세션에 해당 이메일과 토큰 등록
//        if (userInfo.get("email") != null) {
//            session.setAttribute("userId", userInfo.get("email"));
//            session.setAttribute("access_Token", access_Token);
//        }

        return "kakao login test.";
    }


    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        return ResponseEntity.ok(userService.reissue(tokenRequestDto));
    }




//    @GetMapping("/mypage")
//    public ResponseEntity<UserResponseDto> getMyInfo() {    //email 만 반환
//        return ResponseEntity.ok(userService.getMyInfo());
//    }

    //아직 쓸 데 없음
//    @GetMapping("/{email}")
//    public ResponseEntity<UserResponseDto> getUserInfo(@PathVariable String email) {
//        return ResponseEntity.ok(userService.getUserInfo(email));
//    }
}
