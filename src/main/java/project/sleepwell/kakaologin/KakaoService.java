package project.sleepwell.kakaologin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.sleepwell.config.MyConfigurationProperties;
import project.sleepwell.domain.user.Authority;
import project.sleepwell.domain.user.User;
import project.sleepwell.domain.user.UserRepository;
import project.sleepwell.web.dto.LoginDto;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoService {

    private final KakaoOAuth2 kakaoOAuth2;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final MyConfigurationProperties myConfigurationProperties;


    public LoginDto kakaoLogin(String code) {
        KakaoUserInfo kakaoUserInfo = kakaoOAuth2.getUserInfo(code);

        Long kakaoId = kakaoUserInfo.getId();
        String email = kakaoUserInfo.getEmail();
        String nickname = kakaoUserInfo.getNickname();

        String username = nickname;

        //우리 DB에 중복된 Kakao Id 가 있는지 확인
        User kakaoUser = userRepository.findByKakaoId(kakaoId)
                .orElse(null);

        LoginDto loginDto = null;


        if(kakaoUser != null){
            //user 가 존재한다면? (카카오 로그인을 이용하는 우리 서비스 회원)
            String password = kakaoId + myConfigurationProperties.getAdminToken();
            String encodedPassword = passwordEncoder.encode(password);
            loginDto = new LoginDto(kakaoUser.getEmail(), encodedPassword);
            return loginDto;

        } else if (kakaoUser == null) {
            //카카오 정보로 회원가입 (null 이면 가입)
            String password = kakaoId + myConfigurationProperties.getAdminToken();
            log.info("here!!! admin token = {}", myConfigurationProperties.getAdminToken());
            //패스워드 인코딩
            String encodedPassword = passwordEncoder.encode(password);
            // ROLE = 사용자
            Authority authority = Authority.ROLE_USER;

            //오류 고칠 것==========================//
            kakaoUser = new User(username, encodedPassword, email, authority, kakaoId);
            log.info("kakao user = {}", kakaoUser);     //정상적으로 저장이 될테고
            userRepository.save(kakaoUser);

            loginDto = new LoginDto(email, password);

            return loginDto;
        }

        return loginDto;
    }
}