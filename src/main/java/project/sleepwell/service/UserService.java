package project.sleepwell.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.sleepwell.analisys.LineChartRepository;
import project.sleepwell.web.dto.LineChartResponseDto;
import project.sleepwell.config.MyConfigurationProperties;
import project.sleepwell.domain.cards.Cards;
import project.sleepwell.domain.cards.CardsRepository;
import project.sleepwell.domain.refreshtoken.RefreshToken;
import project.sleepwell.domain.refreshtoken.RefreshTokenRepository;
import project.sleepwell.domain.user.*;
import project.sleepwell.jwt.JwtTokenProvider;
import project.sleepwell.kakaologin.KakaoOAuth2;
import project.sleepwell.util.SecurityUtil;
import project.sleepwell.web.dto.LoginDto;
import project.sleepwell.web.dto.SignupRequestDto;
import project.sleepwell.web.dto.TokenDto;
import project.sleepwell.web.dto.TokenRequestDto;
import java.time.LocalDate;
import java.util.*;

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

    private final CardsRepository cardRepository;
    private final ChartService chartService;
    private final LineChartRepository lineChartRepository;

    @Autowired
    MyConfigurationProperties myConfigurationProperties;


    @Transactional
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
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        //토큰 만들기
        TokenDto tokenDto = jwtTokenProvider.generateTokenDto(authentication);
        //refresh token 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .refreshKey(authentication.getName())
                .refreshValue(tokenDto.getRefreshToken())
                .build();

        
        refreshTokenRepository.save(refreshToken);

        return tokenDto;
    }



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
        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        //토큰 발급
        return tokenDto;

    }


    //username 중복 확인
    @Transactional(readOnly = true)
    public boolean checkUsername(String username) {
        return userRepository.existsByUsername(username);
    }



    //적정 수면 시간, 주간 수면 시간 비교
    public List<LineChartResponseDto> compareToSleeptime(LocalDate today,
                                                         org.springframework.security.core.userdetails.User principal) {

        User user = userRepository.findByUsername(SecurityUtil.getCurrentUsername()).orElseThrow(
                () -> new IllegalArgumentException("There is no user by that name.")
        );

        //today: 2021.05.09
        //aWeekAgo : 2021.05.02
        LocalDate aWeekAgo = today.minusDays(6);

        //오늘까지라는 기간 설정을 해야 해
        List<Cards> cardsForSevenDays = lineChartRepository.findCardsBySelectedAtBetweenAndUser(aWeekAgo, today, user);
        List<LineChartResponseDto> lineChart = new ArrayList<>();


        //일주일치 카드 뽑기. 최대 카드 7개./////////////////-> 날짜가 5월 2일부터 5월 9일까지 8개 카드가 나옴
        for (Cards card : cardsForSevenDays) {  //1, 2, 3, 4, 5, 6, 7, 8, 9, 10
            LocalDate date = card.getSelectedAt();  //날짜
            log.info("일주일 치 카드 뽑았을 때 날짜 = {}", date.toString());      ///////
            Long sleepHour = card.getTotalSleepHour();
            Long sleepMinute = card.getTotalSleepMinute();

            long convertToMin = (sleepHour * 60) + sleepMinute;
            log.info("각 카드의 수면 시간(분으로 환산) = {}", convertToMin);
            double totalTime = Math.floor((convertToMin / 60.0) * 10) / 10; //주간수면시간

            //시간 설정을 안해주면, 1,3,4,7,13 일의 데이터가 있을 때, 기간 조회를 1 ~7일까지로 하면 13일 데이터까지 포함돼서 평균이 나옴.
            //유저가 작성한 카드를 평균 내기 때문.

            //카드에 적힌 날짜 기준으로, 그 날짜보다 이전에 쓴 모든 카드 조회
            //9일을 넣었다면, 5월 9일 이전에 쓴 모든 카드 가져오기.
            double adequateSleepTime = adequateSleepTimeOfToday(date, user);

            LineChartResponseDto lineChartResponseDto = new LineChartResponseDto(date, totalTime, adequateSleepTime);
            lineChart.add(lineChartResponseDto);

        }
        return lineChart;


    }


    //답답해서 테스트 하려고 만든 메서드
    public List<User> getAllUsers() {
        List<User> all = userRepository.findAll();
        return all;
    }



    //오늘의 적정 수면시간 구하기
    public double adequateSleepTimeOfToday(LocalDate today, User user) {
        //오늘을 기준으로 어제까지 작성한 카드의 데이터가 전부 출력 되야 함.
        //5월 2일 이전에 쓴 컨디션이 3,4,5인 모든 카드들 조회
        List<Cards> cardsByToday = lineChartRepository.findCardsByConditionsGreaterThanAndSelectedAtBeforeAndUserEquals(2L, today, user);
        //컨디션이 3, 4, 5인 카드들
        for (Cards cards : cardsByToday) {
            log.info("컨디션이 3,4,5인 오늘 날짜 이전에 쓴 카드들 날짜 = {}" ,cards.getSelectedAt().toString());
        }

        long total = 0;
        int count3 = 0;
        int count4 = 0;
        int count5 = 0;
        for (Cards card : cardsByToday) {
            int condition = card.getConditions().intValue();
            switch (condition) {
                case 3:
                    count3 += 1;
                    Long sleepHour3 = card.getTotalSleepHour();
                    Long sleepMinute3 = card.getTotalSleepMinute();
                    total += (sleepHour3 * 60) + sleepMinute3;
                    System.out.println("total = " + total);
                    break;

                case 4:
                    count4 += 1;
                    Long sleepHour4 = card.getTotalSleepHour();
                    Long sleepMinute4 = card.getTotalSleepMinute();
                    total += (sleepHour4 * 60) + sleepMinute4;
                    System.out.println("total = " + total);
                    break;

                case 5:
                    count5 += 1;
                    Long sleepHour5 = card.getTotalSleepHour();
                    Long sleepMinute5 = card.getTotalSleepMinute();
                    total += (sleepHour5 * 60) + sleepMinute5;
                    System.out.println("total = " + total);
                    break;
            }
        }//

        log.info("count3 = {}", count3);
        log.info("count4 = {}", count4);
        log.info("count5 = {}", count5);


        double theNumOfCon3 = count3;
        double theNumOfCon4 = count4;
        double theNumOfCon5 = count5;
        try {
            if (count3 == 0) {
                theNumOfCon3 = 0;
                System.out.println("theNumOfCon3 = " + theNumOfCon3);;
            } else if (count3 > 0) {
                theNumOfCon3 = theNumOfCon3 / 2 ;
                System.out.println("theNumOfCon3 = " + theNumOfCon3);
            }

            if (count5 == 0) {
                theNumOfCon5 = 0;
                System.out.println("theNumOfCon5 = " + theNumOfCon5);
            } else if (count5 > 0) {
                theNumOfCon5 = (theNumOfCon5 * 3) / 2;
                System.out.println("theNumOfCon5 = " + theNumOfCon5);
            }

            System.out.println("theNumOfCon4 = " + theNumOfCon4);

            double conditions = theNumOfCon3 + theNumOfCon4 + theNumOfCon5;

            log.info("count3 갯수={}, count4 갯수 = {}, count5 갯수 = {}", count3, count4, count5);
            log.info("conditions = {}", conditions);

            total = (long) (total / conditions);
            log.info("분을 카드 갯수로 나눴을 때 최종 분 = {}", total);


        } catch (ArithmeticException e) {
            return 0;
        }

        double adqSleeptime = Math.floor((total / 60.0) * 10) / 10;
        log.info("적정 수면시간 = {} ", adqSleeptime);

        return adqSleeptime;
    }


}   //닫는 최종 괄호
