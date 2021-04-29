package project.sleepwell.controller;

import com.nimbusds.oauth2.sdk.TokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.sleepwell.dto.*;
import project.sleepwell.repository.UserRepository;
import project.sleepwell.service.UserService;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;


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
