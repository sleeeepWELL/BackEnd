package project.sleepwell.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.sleepwell.dto.LoginDto;
import project.sleepwell.dto.SignupRequestDto;
import project.sleepwell.dto.UserResponseDto;
import project.sleepwell.repository.UserRepository;
import project.sleepwell.service.UserService;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;



    //email, username, password
    @PostMapping("/signup")
    public Long createUser(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        return userService.createUser(signupRequestDto);
    }

    //login
    @PostMapping("/login")
    public Long login(@RequestBody LoginDto loginDto) {
        return userService.login(loginDto);
    }


    @GetMapping("/mypage")
    public ResponseEntity<UserResponseDto> getMyInfo() {    //email 만 반환
        return ResponseEntity.ok(userService.getMyInfo());
    }

    //아직 쓸 데 없음
    @GetMapping("/{email}")
    public ResponseEntity<UserResponseDto> getUserInfo(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserInfo(email));
    }
}
