package project.sleepwell.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.sleepwell.config.MyConfigurationProperties;
import project.sleepwell.domain.refreshtoken.RefreshToken;
import project.sleepwell.domain.refreshtoken.RefreshTokenRepository;
import project.sleepwell.domain.user.*;
import project.sleepwell.jwt.JwtTokenProvider;
import project.sleepwell.util.SecurityUtil;
import project.sleepwell.web.dto.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    MyConfigurationProperties myConfigurationProperties;


    //sign up
    @Transactional
    public Long createUser(SignupRequestDto signupRequestDto) {

        String validatedEmail = signupRequestDto.getEmail().replaceAll(" ", "");

        if (userRepository.existsByEmail(validatedEmail)) {
            throw new RuntimeException("이미 사용 중인 이메일 입니다.");
        }

        if (!signupRequestDto.getPassword().equals(signupRequestDto.getPasswordCheck())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        User user = signupRequestDto.toUser(passwordEncoder, validatedEmail);
        return userRepository.save(user).getId();

    }

    //login
    @Transactional
    public TokenDto login(LoginDto loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken = loginDto.toAuthentication();

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        TokenDto tokenDto = jwtTokenProvider.generateTokenDto(authentication);

        RefreshToken refreshToken = RefreshToken.builder()
                .refreshKey(authentication.getName())
                .refreshValue(tokenDto.getRefreshToken())
                .build();

        
        refreshTokenRepository.save(refreshToken);

        return tokenDto;
    }



    //reissue
    @Transactional
    public TokenDto reissue(TokenRequestDto tokenRequestDto) {

        if (!jwtTokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        RefreshToken refreshToken = refreshTokenRepository.findByRefreshKey(authentication.getName())
                .orElseThrow(
                        () -> new RuntimeException("로그아웃 한 사용자 입니다.")
                );

        if (!refreshToken.getRefreshValue().equals(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }


        TokenDto tokenDto = jwtTokenProvider.generateTokenDto(authentication);
        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        return tokenDto;

    }


    //username 가져오기
    public String getUsername() {
        User user = userRepository.findById(SecurityUtil.getCurrentUserId()).orElseThrow(
                () -> new IllegalArgumentException("There is no user.")
        );
        return user.getUsername();
    }

    //username 중복 확인
    @Transactional(readOnly = true)
    public boolean checkUsername(String username) {
        return userRepository.existsByUsername(username);
    }



    //답답해서 테스트 하려고 만든 메서드
    public List<User> getAllUsers() {
        List<User> all = userRepository.findAll();
        return all;
    }

    //회원 탈퇴
    @Transactional
    public void deleteUser() {
        User user = userRepository.findById(SecurityUtil.getCurrentUserId()).orElseThrow(
                () -> new IllegalArgumentException("There is no user.")
        );
        userRepository.deleteById(user.getId());

    }


    //비밀번호 찾기 (새비밀번호 설정)
    @Transactional
    public void setPassword(PasswordRequestDto requestDto) {
        if (!requestDto.getPassword().equals(requestDto.getPasswordCheck())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        User user = userRepository.findByEmail(requestDto.getEmail()).orElseThrow(
                () -> new IllegalArgumentException("There is no user.")
        );
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        user.updatePassword(encodedPassword);

    }

    //username 변경
    @Transactional
    public void changeUsername(Map<String, String> param) {
        User user = userRepository.findById(SecurityUtil.getCurrentUserId())
                .orElseThrow(() -> new IllegalArgumentException("There is no user.")
                );
        user.updateUsername(param.get("username"));
    }


    //비밀번호 변경
   @Transactional
    public void changePassword(Map<String, String> param) {

        if (!param.get("password").equals(param.get("passwordCheck"))) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        User user = userRepository.findById(SecurityUtil.getCurrentUserId()).orElseThrow(
                () -> new IllegalArgumentException("There is no user.")
        );

        String encodedPassword = passwordEncoder.encode(param.get("password"));

        user.updatePassword(encodedPassword);
    }


}   //닫는 최종 괄호
