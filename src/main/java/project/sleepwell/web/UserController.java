package project.sleepwell.web;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import project.sleepwell.config.MyConfigurationProperties;
import project.sleepwell.kakaologin.KakaoService;
import project.sleepwell.service.UserService;
import project.sleepwell.web.dto.*;
import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final KakaoService kakaoService;

    @Autowired
    MyConfigurationProperties myConfigurationProperties;


    //현재 로그인 한 user 의 username 조회
    @GetMapping("/username")
    public String getUsername() {
        return userService.getUsername();
    }

    //username 중복 체크
    @GetMapping("/username/{username}")
    public ResponseEntity<Boolean> checkUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.checkUsername(username));
    }

    //signup
    @PostMapping("/signup")
    public Long createUser(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        return userService.createUser(signupRequestDto);
    }

    //login
    @PostMapping("/api/login")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(userService.login(loginDto));
    }

    //kakao
    @RequestMapping("/oauth/callback/kakao")
    public ResponseEntity<TokenDto> kakaoLogin(@RequestParam String code) {
        TokenDto tokenDto = kakaoService.kakaoLogin(code);
        return ResponseEntity.ok(tokenDto);
    }


    /**
     * 혼자 하는 테스트
     * POST 메서드로 하니까 못 받네. 405 에러
     */
//    @GetMapping("/oauth/callback/kakao")
//    public ResponseEntity<TokenDto> kakaoLogin(String code) {
//        TokenDto tokenDto = kakaoService.kakaoLogin(code);
//        return ResponseEntity.ok(tokenDto);
//    }


    //find password (사실상 새 비밀번호 설정)
    @PutMapping("/setting/password")
    public ResponseEntity<String> setPassword(@RequestBody PasswordRequestDto requestDto) {
        userService.setPassword(requestDto);
        return ResponseEntity.ok("ok");
    }

    //username 변경하기
    @PutMapping("/setting/username")
    public ResponseEntity<String> changeUsername(@RequestBody Map<String, String> param) {
        userService.changeUsername(param);
        return ResponseEntity.ok("ok");
    }

    //change password
    @PutMapping("/setting/password/new")
    public ResponseEntity<String> changePassword(@RequestBody Map<String, String> param) {
        userService.changePassword(param);
        return ResponseEntity.ok("ok");
    }

    //토큰 재발행
    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        return ResponseEntity.ok(userService.reissue(tokenRequestDto));
    }

    //회원 탈퇴
    @DeleteMapping("/withdrawal/membership")
    public ResponseEntity<String> deleteUser() {
        userService.deleteUser();
        return ResponseEntity.ok("ok");
    }

}
