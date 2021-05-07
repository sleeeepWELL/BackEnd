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
import project.sleepwell.web.dto.LoginDto;
import project.sleepwell.web.dto.TokenDto;

import java.util.Collections;
import java.util.Optional;

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


//    public LoginDto kakaoLogin(String code) {
//        KakaoUserInfo kakaoUserInfo = kakaoOAuth2.getUserInfo(code);
//
//        Long kakaoId = kakaoUserInfo.getId();
//        String email = kakaoUserInfo.getEmail();
//        String nickname = kakaoUserInfo.getNickname();
//
//        String username = nickname;
//
//        //우리 DB에 중복된 Kakao Id 가 있는지 확인
//        User kakaoUser = userRepository.findByKakaoId(kakaoId)
//                .orElse(null);
//
//        LoginDto loginDto = null;
//
//
//        if(kakaoUser != null){
//            //user 가 존재한다면? (카카오 로그인을 이용하는 우리 서비스 회원)
//            String password = kakaoId + myConfigurationProperties.getAdminToken();
//            String encodedPassword = passwordEncoder.encode(password);
//            loginDto = new LoginDto(kakaoUser.getEmail(), encodedPassword);
//            return loginDto;
//
//        } else if (kakaoUser == null) {
//            //카카오 정보로 회원가입 (null 이면 가입)
//            String password = kakaoId + myConfigurationProperties.getAdminToken();
//            log.info("here!!! admin token = {}", myConfigurationProperties.getAdminToken());
//            //패스워드 인코딩
//            String encodedPassword = passwordEncoder.encode(password);
//            // ROLE = 사용자
//            Authority authority = Authority.ROLE_USER;
//
//            //오류 고칠 것==========================//
//            kakaoUser = new User(username, encodedPassword, email, authority, kakaoId);
//            log.info("kakao user = {}", kakaoUser);     //정상적으로 저장이 될테고
//            userRepository.save(kakaoUser);
//
//            loginDto = new LoginDto(email, password);
//
//            return loginDto;
//        }
//
//        return loginDto;
//    }



    //카카오 테스트
    public TokenDto kakaoLogin(String code) {
        KakaoUserInfo kakaoUserInfo = kakaoOAuth2.getUserInfo(code);
        Long kakaoId = kakaoUserInfo.getId();
        String nickname = kakaoUserInfo.getNickname();
        String email = kakaoUserInfo.getEmail();

        //우리 DB에 중복된 Kakao Id 가 있는지 확인
        User kakaoUser = userRepository.findByKakaoId(kakaoId)
                .orElse(null);


        //슬립웰에 처음 들어온 유저. 카카오 정보로 슬립웰에 회원가입 시킨다.
        if (kakaoUser == null) {
            User sameUser = null;
            //카카오 이메일이 슬립웰 db 에 존재할 때
            if(email.equals(userRepository.findByEmail(email))){  //에러: if 조건문 안 다니까 처음 들어온 유저이면서 이메일도 없을 때 not found user email 메세지가 뜨고 500 에러
                sameUser = userRepository.findByEmail(email).orElseThrow(
                        () -> new IllegalArgumentException("not found kakao user email ")
                );
            }
            //카카오 이메일로 회원 정보를 찾았는데, 슬립웰에서 같은 이메일을 사용하는 유저가 있을 때.
            if (sameUser != null) {
                //슬립웰 유저 == 카카오 유저. 같은 유저라고 가정(이메일 중복확인은 반드시 필요)
//                new IllegalArgumentException("이미 사용중인 이메일입니다. 슬립웰 로그인을 이용하십시오.");
                //이메일 인증 했을 경우 (슬립웰 유저 정보 + 카카오 아이디만 넣으면 된다.)
                kakaoUser = sameUser;
                kakaoUser.setKakaoId(kakaoId);
                userRepository.save(kakaoUser);
            } else {
                //슬립웰에 처음 들어온 유저.
                //카카오에서의 닉네임 == 슬립웰에서의 유저네임
                String username = nickname;
                String password = kakaoId + myConfigurationProperties.getAdminToken();
                //패스워드 인코딩
                String encodedPassword = passwordEncoder.encode(password);
                // ROLE = 사용자
                Authority authority = Authority.ROLE_USER;

                kakaoUser = new User(username, encodedPassword, email, authority, kakaoId);
                log.info("kakao user = {}", kakaoUser);     //정상적으로 저장이 될테고

                userRepository.save(kakaoUser);
            }

        }

        //카카오 로그인으로 슬립웰을 이용하는 유저
        //스프링 시큐리티를 통해 로그인 처리
        //카카오 유저 == 나의 유저를 시큐리티 유저와 매핑 시켜야 해 ( User kakaoUser )
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(kakaoUser.getAuthority().toString());
        UserDetails principal = new org.springframework.security.core.userdetails.User(
                kakaoUser.getUsername(),
                //password 할 건지, encodedPassword 할 건지 db 에 어떻게 들어가는지 확인할 것
                kakaoUser.getPassword(),
                Collections.singleton(grantedAuthority));       //이렇게만 해주면 현재 로그인한 회원에 카카오 유저가 연결이 돼있는 건가?

        //왜 email 이 아니고 principal 객체를 넘겨야 하는 건지
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //프론트로 토큰 넘겨주기
        TokenDto tokenDto = jwtTokenProvider.generateTokenDto(authentication);
        //refresh token 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .refreshKey(authentication.getName())
                .refreshValue(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);
        return tokenDto;

    }
}