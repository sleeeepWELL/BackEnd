package project.sleepwell.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.sleepwell.domain.user.Authority;
import project.sleepwell.domain.refreshtoken.RefreshToken;
import project.sleepwell.domain.user.User;
import project.sleepwell.web.dto.LoginDto;
import project.sleepwell.web.dto.SignupRequestDto;
import project.sleepwell.web.dto.TokenDto;
import project.sleepwell.web.dto.TokenRequestDto;
import project.sleepwell.jwt.JwtTokenProvider;
import project.sleepwell.kakaologin.KakaoOAuth2;
import project.sleepwell.kakaologin.KakaoUserInfo;
import project.sleepwell.domain.refreshtoken.RefreshTokenRepository;
import project.sleepwell.domain.user.UserRepository;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    private final KakaoOAuth2 kakaoOAuth2;
    private final AuthenticationManager authenticationManager;
    private static final String ADMIN_TOKEN = "sample1234asdf";

    /**
     * create user
     * email, username, password, passwordCheck
     */
    public Long createUser(SignupRequestDto signupRequestDto) {
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

        //
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //토큰 만들기
        TokenDto tokenDto = jwtTokenProvider.generateTokenDto(authentication);

        //refresh token 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .key(authentication.getName())
                .value(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);  //key, value

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
        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
                .orElseThrow(
                        () -> new RuntimeException("로그아웃 한 사용자 입니다.")
                );
        //일치 여부 검사
        if (!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        //새 토큰 생성
        TokenDto tokenDto = jwtTokenProvider.generateTokenDto(authentication);

        //refresh token 저장하기
        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        //토큰 발급
        return tokenDto;


    }

    //kakao
    public void kakaoLogin(String code) {
        //카카오 OAuth2 를 통해 카카오 사용자 정보 조회
        KakaoUserInfo userInfo = kakaoOAuth2.getUserInfo(code);
        Long kakaoId = userInfo.getId();
        String nickname = userInfo.getNickname();
        String email = userInfo.getEmail();

        //우리 db 에서 회원 id 와 패스워드. 회원 id == 카카오 nickname
        String username = nickname;
        //패스워드 == 카카오 id + admin token  //==비밀번호 저장 방법 찾기==//
        String password = kakaoId + ADMIN_TOKEN;

        // DB에 중복된 Kakao Id 가 있는지 확인
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
            userRepository.save(kakaoUser);
        }

        //로그인 처리
        Authentication kakaoUsernamePassword = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(kakaoUsernamePassword);
        SecurityContextHolder.getContext().setAuthentication(authentication);


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
