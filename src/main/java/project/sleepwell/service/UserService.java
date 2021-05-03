package project.sleepwell.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.sleepwell.config.MyConfigurationProperties;
import project.sleepwell.domain.cards.CardsRepository;
import project.sleepwell.domain.refreshtoken.RefreshToken;
import project.sleepwell.domain.refreshtoken.RefreshTokenRepository;
import project.sleepwell.domain.user.Authority;
import project.sleepwell.domain.user.User;
import project.sleepwell.domain.user.UserRepository;
import project.sleepwell.jwt.JwtTokenProvider;
import project.sleepwell.kakaologin.KakaoOAuth2;
import project.sleepwell.kakaologin.KakaoUserInfo;
import project.sleepwell.web.dto.LoginDto;
import project.sleepwell.web.dto.SignupRequestDto;
import project.sleepwell.web.dto.TokenDto;
import project.sleepwell.web.dto.TokenRequestDto;

import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;    //비밀번호 검증
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    private final KakaoOAuth2 kakaoOAuth2;
    private final AuthenticationManager authenticationManager;
//    private static final String ADMIN_TOKEN = "sample1234asdf";

    private final CardsRepository cardRepository;

    @Autowired
    MyConfigurationProperties myConfigurationProperties;

    /**
     * create user
     * email, username, password, passwordCheck
     */
    @Transactional
    public Long createUser(SignupRequestDto signupRequestDto) {
        //따로 메서드로 빼도 됨
        if (userRepository.existsByEmail(signupRequestDto.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일 입니다.");
        }

        if (!signupRequestDto.getPassword().equals(signupRequestDto.getPasswordCheck())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        User user = signupRequestDto.toUser(passwordEncoder);
        return userRepository.save(user).getId();

    }

    @Transactional
    public TokenDto login(LoginDto loginDto) {
        //email, password 를 인자로 받아서 authenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = loginDto.toAuthentication();

        //authentication = id, password(검증된), authority
        //authentication = username, password(검증된), authority
        //== principal, credential, authority
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //토큰 만들기
        TokenDto tokenDto = jwtTokenProvider.generateTokenDto(authentication);  //====//

        //refresh token 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .refreshKey(authentication.getName())
                .refreshValue(tokenDto.getRefreshToken())
                .build();

        
        refreshTokenRepository.save(refreshToken);
        //refresh token 재발행 테스트용 (데이터 확인하고 삭제할 것)

        //토큰 발급
        return tokenDto;
    }


    /**
     * 토큰 재발행
     * @param tokenRequestDto
     * @return
     */
    @Transactional
    public TokenDto reissue(TokenRequestDto tokenRequestDto) {
        //refresh token 검증 하기
        if (!jwtTokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
        }

        //access token 을 이용해 authentication 객체 리턴
        Authentication authentication = jwtTokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        //refresh Token 가져오기
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshKey(authentication.getName())
                .orElseThrow(
                        () -> new RuntimeException("로그아웃 한 사용자 입니다.")
                );
        //일치 여부 검사
        if (!refreshToken.getRefreshValue().equals(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        //새 토큰 생성
        TokenDto tokenDto = jwtTokenProvider.generateTokenDto(authentication);

        //refresh token 업데이트 (업데이트 할 때마다, 계속 데이터 쌓이는지 확인) -> 업데이트로 value 값 바뀌는 것 확인
        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        //토큰 발급
        return tokenDto;

    }

    //kakao
//    public TokenDto kakaoLogin(String code) {
//        //카카오 OAuth2 를 통해 카카오 사용자 정보 조회
//        //kakao user info : id, email, nickname
//        KakaoUserInfo userInfo = kakaoOAuth2.getUserInfo(code);
//        Long kakaoId = userInfo.getId();
//        String nickname = userInfo.getNickname();
//        String email = userInfo.getEmail();
//
//        //우리 db 에서 회원 id 와 패스워드. 회원 id(String) == 카카오 nickname
//        String username = nickname;
//        //패스워드 == 카카오 id + admin token  //==비밀번호 저장 방법 찾기==//
////        String password = kakaoId + ADMIN_TOKEN;
//        String password = kakaoId + myConfigurationProperties.getAdminToken();
//        log.info("here!!! admin token = {}", myConfigurationProperties.getAdminToken());
//
//        //우리 DB에 중복된 Kakao Id 가 있는지 확인
//        User kakaoUser = userRepository.findByKakaoId(kakaoId)
//                .orElse(null);
//
//        //카카오 정보로 회원가입 (null 이면 가입)
//        if (kakaoUser == null) {
//            //패스워드 인코딩
//            String encodedPassword = passwordEncoder.encode(password);
//            // ROLE = 사용자
//            Authority authority = Authority.ROLE_USER;
//
//            //오류 고칠 것==========================//
//            kakaoUser = new User(username, encodedPassword, email, authority, kakaoId);
//            log.info("kakao user = {}", kakaoUser);     //정상적으로 저장이 될테고
//            userRepository.save(kakaoUser);
//        }
//
//        //스프링 시큐리티를 통해 로그인 처리
//        //카카오 유저 == 나의 유저를 시큐리티 유저와 매핑 시켜야 해 ( User kakaoUser )
//        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(kakaoUser.getAuthority().toString());
//        UserDetails principal = new org.springframework.security.core.userdetails.User(
//                kakaoUser.getUsername(),
//                kakaoUser.getPassword(),
//                Collections.singleton(grantedAuthority));       //이렇게만 해주면 현재 로그인한 회원에 카카오 유저가 연결이 돼있는 건가?
//
//        //왜 email 이 아니고 principal 객체를 넘겨야 하는 건지
//        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        //프론트로 토큰 넘겨주기
//        TokenDto tokenDto = jwtTokenProvider.generateTokenDto(authentication);
//        //refresh token 저장
//        RefreshToken refreshToken = RefreshToken.builder()
//                .refreshKey(authentication.getName())
//                .refreshValue(tokenDto.getRefreshToken())
//                .build();
//
//        refreshTokenRepository.save(refreshToken);
//
//        //프론트에 응답 코드 주기 (tokenDto 넘기면서) -> controller 가서 해야 할 듯
//        return tokenDto;
//
//    }

    //프론트에서 보내주는 토큰 값으로 카카오 사용자 정보 가져오기
    public TokenDto kakaoLogin(String token) {
        KakaoUserInfo userInfo = kakaoOAuth2.getUserInfo(token);

        Long kakaoId = userInfo.getId();
        String nickname = userInfo.getNickname();
        String email = userInfo.getEmail();

        //우리 db 에서 회원 id 와 패스워드. 회원 id(String) == 카카오 nickname
        String username = nickname;
        //패스워드 == 카카오 id + admin token  //==비밀번호 저장 방법 찾기==//
//        String password = kakaoId + ADMIN_TOKEN;
        String password = kakaoId + myConfigurationProperties.getAdminToken();
        log.info("here!!! admin token = {}", myConfigurationProperties.getAdminToken());

        //우리 DB에 중복된 Kakao Id 가 있는지 확인
        User kakaoUser = userRepository.findByKakaoId(kakaoId)
                .orElse(null);

        //카카오 정보로 회원가입 (null 이면 가입)
        if (kakaoUser == null) {
            //패스워드 인코딩
            String encodedPassword = passwordEncoder.encode(password);
            // ROLE = 사용자
            Authority authority = Authority.ROLE_USER;

            //오류 고칠 것==========================//
            kakaoUser = new User(username, encodedPassword, email, authority, kakaoId);
            log.info("kakao user = {}", kakaoUser);     //정상적으로 저장이 될테고
            userRepository.save(kakaoUser);
        }

        //스프링 시큐리티를 통해 로그인 처리
        //카카오 유저 == 나의 유저를 시큐리티 유저와 매핑 시켜야 해 ( User kakaoUser )
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(kakaoUser.getAuthority().toString());
        UserDetails principal = new org.springframework.security.core.userdetails.User(
                kakaoUser.getUsername(),
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

        //프론트에 응답 코드 주기 (tokenDto 넘기면서) -> controller 가서 해야 할 듯
        return tokenDto;

    }

    //== 체크 메서드 ==//
//    public void isAvailable(String email) {
//        if (userRepository.findByEmail(email).isPresent()) {
//            throw new IllegalArgumentException("이미 사용 중인 이메일 입니다.");
//        }
//    }

    //처음부터 인자로 email 을 받아서 유저 정보를 찾고, email 다시 반환
//    @Transactional(readOnly = true)
//    public UserResponseDto getUserInfo(String email) {
//        return userRepository.findByEmail(email)
//                .map(UserResponseDto::of)       //stream
//                .orElseThrow(
//                        () -> new RuntimeException("유저 정보가 존재하지 않습니다.")
//                );
//    }

    //SecurityUtil 에서 로그인 한 유저 정보를 찾아서 email 만 반환
//    @Transactional(readOnly = true)
//    public UserResponseDto getMyInfo() {    //username 을 반환하는 게 의미가 없어서 id 만 반환하게 함
//        return userRepository.findById(SecurityUtil.getCurrentUserId())
//                .map(UserResponseDto::of)
//                .orElseThrow(
//                        () -> new RuntimeException("로그인 한 유저의 정보가 존재하지 않습니다.")
//                );
//    }

}   //닫는 최종 괄호
