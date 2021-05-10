//package project.sleepwell.analisys;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import project.sleepwell.domain.cards.Cards;
//import project.sleepwell.domain.user.User;
//import project.sleepwell.domain.user.UserRepository;
//import project.sleepwell.util.SecurityUtil;
//import project.sleepwell.web.dto.LineChartResponseDto;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//@RequiredArgsConstructor
//@Slf4j
//@Service
//public class LineChartService {
//
//    private final LineChartRepository lineChartRepository;
//    private final UserRepository userRepository;
//
//    //적정 수면 시간, 주간 수면 시간 비교
//    public List<LineChartResponseDto> compareToSleeptime(LocalDate today,
//                                                         org.springframework.security.core.userdetails.User principal) {
//
//        User user = userRepository.findByUsername(SecurityUtil.getCurrentUsername()).orElseThrow(
//                () -> new IllegalArgumentException("There is no user by that name.")
//        );
//
//        //today: 2021.05.09
//        //aWeekAgo : 2021.05.03
//        LocalDate aWeekAgo = today.minusDays(6);
//
//        //오늘까지라는 기간 설정을 해야 해
//        List<Cards> cardsForSevenDays = lineChartRepository.findCardsBySelectedAtBetweenAndUser(aWeekAgo, today, user);
//        List<LineChartResponseDto> lineChart = new ArrayList<>();
//
//
//        //일주일치 카드 뽑기. 최대 카드 7개./////////////////-> 날짜가 5월 2일부터 5월 9일까지 8개 카드가 나옴
//        for (Cards card : cardsForSevenDays) {  //1, 2, 3, 4, 5, 6, 7, 8, 9, 10
//            LocalDate date = card.getSelectedAt();  //날짜
//            log.info("일주일 치 카드 뽑았을 때 날짜 = {}", date.toString());      ///////
//            Long sleepHour = card.getTotalSleepHour();
//            Long sleepMinute = card.getTotalSleepMinute();
//
//            long convertToMin = (sleepHour * 60) + sleepMinute;
//            log.info("각 카드의 수면 시간(분으로 환산) = {}", convertToMin);
//            double totalTime = Math.floor((convertToMin / 60.0) * 10) / 10; //주간수면시간
//
//            //시간 설정을 안해주면, 1,3,4,7,13 일의 데이터가 있을 때, 기간 조회를 1 ~7일까지로 하면 13일 데이터까지 포함돼서 평균이 나옴.
//            //유저가 작성한 카드를 평균 내기 때문.
//
//            //카드에 적힌 날짜 기준으로, 그 날짜보다 이전에 쓴 모든 카드 조회
//            //9일을 넣었다면, 5월 9일 이전에 쓴 모든 카드 가져오기.
//            double adequateSleepTime = adequateSleepTimeOfToday(date, user);
//
//            LineChartResponseDto lineChartResponseDto = new LineChartResponseDto(date, totalTime, adequateSleepTime);
//            lineChart.add(lineChartResponseDto);
//
//        }
//        return lineChart;
//
//
//    }
//
//    //오늘의 적정 수면시간 구하기
//    public double adequateSleepTimeOfToday(LocalDate today, User user) {
//        //오늘을 기준으로 어제까지 작성한 카드의 데이터가 전부 출력 되야 함.
//        //5월 2일 이전에 쓴 컨디션이 3,4,5인 모든 카드들 조회
//        List<Cards> cardsByToday = lineChartRepository.findCardsByConditionsGreaterThanAndSelectedAtBeforeAndUserEquals(2L, today, user);
//        //컨디션이 3, 4, 5인 카드들
//        for (Cards cards : cardsByToday) {
//            log.info("컨디션이 3,4,5인 오늘 날짜 이전에 쓴 카드들 날짜 = {}" ,cards.getSelectedAt().toString());
//        }
//
//        long total = 0;
//        int count3 = 0;
//        int count4 = 0;
//        int count5 = 0;
//        for (Cards card : cardsByToday) {
//            int condition = card.getConditions().intValue();
//            switch (condition) {
//                case 3:
//                    count3 += 1;
//                    Long sleepHour3 = card.getTotalSleepHour();
//                    Long sleepMinute3 = card.getTotalSleepMinute();
//                    total += (sleepHour3 * 60) + sleepMinute3;
//                    System.out.println("total = " + total);
//                    break;
//
//                case 4:
//                    count4 += 1;
//                    Long sleepHour4 = card.getTotalSleepHour();
//                    Long sleepMinute4 = card.getTotalSleepMinute();
//                    total += (sleepHour4 * 60) + sleepMinute4;
//                    System.out.println("total = " + total);
//                    break;
//
//                case 5:
//                    count5 += 1;
//                    Long sleepHour5 = card.getTotalSleepHour();
//                    Long sleepMinute5 = card.getTotalSleepMinute();
//                    total += (sleepHour5 * 60) + sleepMinute5;
//                    System.out.println("total = " + total);
//                    break;
//            }
//        }//
//
//        log.info("count3 = {}", count3);
//        log.info("count4 = {}", count4);
//        log.info("count5 = {}", count5);
//
//
//        double theNumOfCon3 = count3;
//        double theNumOfCon4 = count4;
//        double theNumOfCon5 = count5;
//        try {
//            if (count3 == 0) {
//                theNumOfCon3 = 0;
//                System.out.println("theNumOfCon3 = " + theNumOfCon3);;
//            } else if (count3 > 0) {
//                theNumOfCon3 = theNumOfCon3 / 2 ;
//                System.out.println("theNumOfCon3 = " + theNumOfCon3);
//            }
//
//            if (count5 == 0) {
//                theNumOfCon5 = 0;
//                System.out.println("theNumOfCon5 = " + theNumOfCon5);
//            } else if (count5 > 0) {
//                theNumOfCon5 = (theNumOfCon5 * 3) / 2;
//                System.out.println("theNumOfCon5 = " + theNumOfCon5);
//            }
//
//            System.out.println("theNumOfCon4 = " + theNumOfCon4);
//
//            double conditions = theNumOfCon3 + theNumOfCon4 + theNumOfCon5;
//
//            log.info("count3 갯수={}, count4 갯수 = {}, count5 갯수 = {}", count3, count4, count5);
//            log.info("conditions = {}", conditions);
//
//            total = (long) (total / conditions);
//            log.info("분을 카드 갯수로 나눴을 때 최종 분 = {}", total);
//
//
//        } catch (ArithmeticException e) {
//            return 0;
//        }
//
//        double adqSleeptime = Math.floor((total / 60.0) * 10) / 10;
//        log.info("적정 수면시간 = {} ", adqSleeptime);
//
//        return adqSleeptime;
//    }
//
//}
