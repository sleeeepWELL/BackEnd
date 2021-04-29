package project.sleepwell.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.sleepwell.dto.UserResponseDto;
import project.sleepwell.service.AuthService;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;


}
