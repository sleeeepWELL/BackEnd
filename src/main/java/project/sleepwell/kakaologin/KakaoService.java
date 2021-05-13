package project.sleepwell.kakaologin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.sleepwell.config.MyConfigurationProperties;
import project.sleepwell.domain.refreshtoken.RefreshToken;
import project.sleepwell.domain.refreshtoken.RefreshTokenRepository;
import project.sleepwell.domain.user.Authority;
import project.sleepwell.domain.user.User;
import project.sleepwell.domain.user.UserRepository;
import project.sleepwell.jwt.JwtTokenProvider;
import project.sleepwell.web.dto.TokenDto;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoService {

    private final KakaoOAuth2 kakaoOAuth2;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final MyConfigurationProperties myConfigurationProperties;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;


    //카카오 로그인
    public TokenDto kakaoLogin(String code) {
        KakaoUserInfo kakaoUserInfo = kakaoOAuth2.getUserInfo(code);
        Long kakaoId = kakaoUserInfo.getId();
        String email = kakaoUserInfo.getEmail();
        String nickname = kakaoUserInfo.getNickname();

        //우리 DB에 중복된 Kakao Id 가 있는지 확인
        User kakaoUser = userRepository.findByKakaoId(kakaoId)
                .orElse(null);


        //슬립웰에 처음 들어온 유저. 카카오 정보로 슬립웰에 회원가입 시킨다.
        if (kakaoUser == null) {
            User sameUser = null;
            //카카오 이메일이 슬립웰 db 에 존재할 때
            if(email.equals(userRepository.findByEmail(email))){
                sameUser = userRepository.findByEmail(email).orElseThrow(
                        () -> new IllegalArgumentException("not found kakao user email ")
                );
            }
            //카카오 이메일로 회원 정보를 찾았는데, 슬립웰에서 같은 이메일을 사용하는 유저가 있을 때.
            if (sameUser != null) {
                kakaoUser = sameUser;
                kakaoUser.setKakaoId(kakaoId);
                userRepository.save(kakaoUser);
            } else {
                //슬립웰에 처음 들어온 유저.
                String username = nickname;
                String password = kakaoId + myConfigurationProperties.getAdminToken();
                String encodedPassword = passwordEncoder.encode(password);
                Authority authority = Authority.ROLE_USER;

                kakaoUser = new User(username, encodedPassword, email, authority, kakaoId);

                userRepository.save(kakaoUser);
            }

        }


        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(kakaoUser.getAuthority().toString());
        UserDetails principal = new org.springframework.security.core.userdetails.User(
                String.valueOf(kakaoUser.getId()),
                kakaoUser.getPassword(),
                Collections.singleton(grantedAuthority));

        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //프론트로 토큰 넘겨주기
        TokenDto tokenDto = jwtTokenProvider.generateTokenDto(authentication);

        RefreshToken refreshToken = RefreshToken.builder()
                .refreshKey(authentication.getName())
                .refreshValue(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

        return tokenDto;
    }

}