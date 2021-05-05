package project.sleepwell.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.sleepwell.config.MyConfigurationProperties;
import project.sleepwell.domain.user.Message;
import project.sleepwell.domain.user.StatusEnum;
import project.sleepwell.domain.user.User;
import project.sleepwell.domain.user.UserRepository;
import project.sleepwell.kakaologin.KakaoOAuth2;
import project.sleepwell.service.UserService;
import project.sleepwell.util.SecurityUtil;
import project.sleepwell.web.dto.LoginDto;
import project.sleepwell.web.dto.SignupRequestDto;
import project.sleepwell.web.dto.TokenDto;
import project.sleepwell.web.dto.TokenRequestDto;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final KakaoOAuth2 kakaoOAuth2;
    private final UserRepository userRepository;

    @Autowired
    MyConfigurationProperties myConfigurationProperties;

    ////////////////////////////
    //현재 로그인 하고 들어온 유저의 정보 뽑기 (테스트용. 개발자용) -> 성공 -> SecurityUtil 바꿔서 안먹힘. 고쳐서 써먹어보자.
    @GetMapping("/test/users")
    public User getUserInfo() {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("nothing"));
        return user;
    }

    @GetMapping("/test/authentication/test")
    public User authTest(@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("There is nobody by that name.")
        );

        return user;
    }

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
        TokenDto tokenDto = userService.login(loginDto);
        return ResponseEntity.ok(tokenDto);

//        return ResponseEntity.ok(userService.login(loginDto));  //token 발행
    }

    //테스트 용
//    @PostMapping("/api/login")
//    public ResponseEntity<String> login(@Valid @RequestBody LoginDto loginDto, HttpServletResponse response) throws IOException {
//        userService.login(loginDto, response);  //token 발행
//        return ResponseEntity.ok().body("ok");
//    }

    //login - client code test
//    @PostMapping("/api/login/code")
//    public ResponseEntity<Message> loginCode(@Valid @RequestBody LoginDto loginDto) throws JsonProcessingException {
//        TokenDto tokenDto = userService.login(loginDto);
//        Message message = new Message();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
//
//        message.setStatus(StatusEnum.OK);
//        message.setMessage("kakaoLoginSuccessCode");
//        message.setData(tokenDto);
//
//        return new ResponseEntity<>(message, headers, HttpStatus.ACCEPTED);
//    }

    //callback 받는 URI
    /**
     * 프론트에서 로그인 요청(oauth/authorize?) -> 로그인 후 동의 ->
     * 카카오 Authorization Server 는 이 uri 에 code 값을 넘겨줄 것이다.
     * code 값으로 token 요청
     */
//    @RequestMapping("/kakaoLogin")  //get mapping
//    public ResponseEntity<Message> kakaoLogin(@RequestParam String code) {
//        TokenDto tokenDto = userService.kakaoLogin(code);
//        //default status == bad request.
//        Message message = new Message();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
//
//        message.setStatus(StatusEnum.OK);
//        message.setMessage("kakaoLoginSuccessCode");
//        message.setData(tokenDto);
//
//        return new ResponseEntity<>(message, headers, HttpStatus.OK);
//    }

    //1.
    //프론트에서 토큰까지 받아서 서버로 토큰 넘겨준다면 (동의하고 계속하기 버튼 누르면서 token 같이 넘겨주면 서버에서 받는다.)
//    @RequestMapping("/kakaoLogin")  //get mapping
//    public ResponseEntity<Message> kakaoLogin(@RequestBody Map<String, Object> token) {
//        TokenDto tokenDto = userService.kakaoLogin(token.get("keyFromFront").toString());
//
//        //default status == bad request.
//        Message message = new Message();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
//
//        message.setStatus(StatusEnum.OK);
//        message.setMessage("kakaoLoginSuccessCode");
//        message.setData(tokenDto);
//
//        return new ResponseEntity<>(message, headers, HttpStatus.OK);
//    }


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
