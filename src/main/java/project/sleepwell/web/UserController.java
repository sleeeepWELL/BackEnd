package project.sleepwell.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.sleepwell.kakaologin.KakaoOAuth2;
import project.sleepwell.service.KakaoService;
import project.sleepwell.service.UserService;
import project.sleepwell.web.dto.LoginDto;
import project.sleepwell.web.dto.SignupRequestDto;
import project.sleepwell.web.dto.TokenDto;
import project.sleepwell.web.dto.TokenRequestDto;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final KakaoService kakaoService;
    private final KakaoOAuth2 kakaoOAuth2;


    @GetMapping("/test")
    public String test() {
        return "test: success.";
    }

    @GetMapping("/only/user")
    public String testUser() {
        return "권한 있는 사람만 보이는 메세지. only user can read.";
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
     * 프론트에서 로그인 요청(oauth/authorize?) -> 로그인 후 동의 ->
     * 카카오 Authorization Server 는 이 uri 에 code 값을 넘겨줄 것이다.
     * code 값으로 token 요청
     */
    @RequestMapping("/kakaoLogin")  //get mapping
    public String kakaoLogin(@RequestParam String code) {

////        String access_Token = kakaoService.getAccessToken(code);
//        String access_Token = kakaoOAuth2.getAccessToken(code);
//        System.out.println("######" + code);
//        System.out.println("access_token: " + access_Token);
//
////        HashMap<String, Object> userInfo = kakaoService.getUserInfo(access_Token);
////        System.out.println("login Controller = " + userInfo);

        userService.kakaoLogin(code);


        return "kakao login: success.";
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
