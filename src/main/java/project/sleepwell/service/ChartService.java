package project.sleepwell.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.sleepwell.domain.cards.Cards;
import project.sleepwell.domain.cards.CardsRepository;
import project.sleepwell.domain.user.User;
import project.sleepwell.domain.user.UserRepository;
import project.sleepwell.util.SecurityUtil;
import project.sleepwell.web.dto.LineChartResponseDto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChartService {
    private final CardsRepository cardsRepository;
    private final UserRepository userRepository;

    //conditons이 2보다 큰 카드에서 sleeptime을 추출하여 평균 계산 -> 가중평균
    public List<Integer> yoursleeptimebyconditions(org.springframework.security.core.userdetails.User principal){
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("nothing")
        );
        List<Cards> cards = cardsRepository.findCardsByConditionsGreaterThanAndUserEquals(2L,user); //컨디션이 2보다 큰 카드

        List<Long> sleeptimes03 = new ArrayList<>(); //컨디션이 보통인 카드들의 수면 시간, 분을 담을 그릇
        List<Long> sleeptimes04 = new ArrayList<>(); //컨디션이 좋음인 카드들의 수면 시간, 분을 담을 그릇
        List<Long> sleeptimes05 = new ArrayList<>(); //컨디션이 매우좋음 카드들의 수면 시간, 분을 담을 그릇

        for(int i = 0; i < cards.size(); i++){ //카드의 개수만큼 반복
            if (cards.get(i).getConditions()==3){
                sleeptimes03.add(cards.get(i).getTotalSleepHour());
                sleeptimes03.add(cards.get(i).getTotalSleepMinute());
            }
            if (cards.get(i).getConditions()==4){
                sleeptimes04.add(cards.get(i).getTotalSleepHour());
                sleeptimes04.add(cards.get(i).getTotalSleepMinute());
            }
            if (cards.get(i).getConditions()==5){
                sleeptimes05.add(cards.get(i).getTotalSleepHour());
                sleeptimes05.add(cards.get(i).getTotalSleepMinute());
            }
        }
        // [수면시간, 분, 수면시간, 분, ... ]

        int totaltime = 0;
        for (int i = 0; i < sleeptimes03.size(); i++){
            if(i%2==0){
                totaltime += sleeptimes03.get(i)*60; // 수면시간은 짝수 인덱스 -> 분으로 변환 위해 60 곱
            }
            if(i%2!=0){
                totaltime += sleeptimes03.get(i); // 수면 분은 홀수 인덱스
            }
        }
        for (int i = 0; i < sleeptimes04.size(); i++){
            if(i%2==0){
                totaltime += sleeptimes04.get(i)*60*2;
            }
            if(i%2!=0){
                totaltime += sleeptimes04.get(i)*2;
            }
        }
        for (int i = 0; i < sleeptimes05.size(); i++){
            if(i%2==0){
                totaltime += sleeptimes05.get(i)*60*3;
            }
            if(i%2!=0){
                totaltime += sleeptimes05.get(i)*3;
            }
        }

        // [3카드],[3카드],[3카드],[3카드],[3카드],[4카드],[4카드],[4카드],[5카드],[5카드],[5카드],[5카드],[5카드]
        // 3카드*5+4카드*3+5카드*5/1*5+2*3+3*5

        try {
            totaltime /= ((sleeptimes03.size() / 2)+sleeptimes04.size()+(3* sleeptimes05.size() / 2));
        }catch (ArithmeticException e){
            List<Integer> zero = new ArrayList<>();
            return zero;
        }

        int yourSleepHour = totaltime / 60;
        int yourSleepMinute = totaltime % 60;

        List<Integer> totalSleeptime = new ArrayList<>();
        totalSleeptime.add(yourSleepHour);
        totalSleeptime.add(yourSleepMinute);

        return totalSleeptime;
    }

    //주간, 월간 태그 빈도수 -> 막대그래프
    public List<List<Integer>> tagbarchart(LocalDate today, org.springframework.security.core.userdetails.User principal){
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("nothing")
        );

        //주간 데이터
        LocalDate weekly = today.minusDays(7L);
        List<Cards> weeklyCards = cardsRepository.findCardsBySelectedAtIsAfterAndUser(weekly,user);
        List<Integer> weeklytags= new ArrayList<>();

        Integer tagExerciseWeekly = 0;
        Integer tagDrinkWeekly = 0;
        Integer tagNightWeekly = 0;
        Integer tagmdSnackWeekly = 0;

        for (int i = 0; i < weeklyCards.size(); i++){
            for (int j = 0; j < weeklyCards.get(i).getTag().size() ; j++){
                if (weeklyCards.get(i).getTag().get(j).contains("운동")){
                    tagExerciseWeekly += 1;
                }
                if (weeklyCards.get(i).getTag().get(j).contains("음주")){
                    tagDrinkWeekly += 1;
                }
                if (weeklyCards.get(i).getTag().get(j).contains("야근")){
                    tagNightWeekly += 1;
                }
                if (weeklyCards.get(i).getTag().get(j).contains("야식")){
                    tagmdSnackWeekly += 1;
                }
            }
        }

        weeklytags.add(tagExerciseWeekly);
        weeklytags.add(tagDrinkWeekly);
        weeklytags.add(tagNightWeekly);
        weeklytags.add(tagmdSnackWeekly);
        weeklytags.add(7);

        // 월간 데이터
        LocalDate monthly = today.minusDays(30L);
        List<Cards> monthlyCards = cardsRepository.findCardsBySelectedAtIsAfterAndUser(monthly,user);
        List<Integer> monthlytags= new ArrayList<>();

        Integer tagExerciseMonthly = 0;
        Integer tagDrinkMonthly = 0;
        Integer tagNightMonthly = 0;
        Integer tagmdSnackMonthly = 0;

        for (int i = 0; i < monthlyCards.size(); i++){
            for (int j = 0; j < monthlyCards.get(i).getTag().size() ; j++){
                if (monthlyCards.get(i).getTag().get(j).contains("운동")){
                    tagExerciseMonthly += 1;
                }
                if (monthlyCards.get(i).getTag().get(j).contains("음주")){
                    tagDrinkMonthly += 1;
                }
                if (monthlyCards.get(i).getTag().get(j).contains("야근")){
                    tagNightMonthly += 1;
                }
                if (monthlyCards.get(i).getTag().get(j).contains("야식")){
                    tagmdSnackMonthly += 1;
                }
            }
        }
        monthlytags.add(tagExerciseMonthly);
        monthlytags.add(tagDrinkMonthly);
        monthlytags.add(tagNightMonthly);
        monthlytags.add(tagmdSnackMonthly);
        monthlytags.add(30);

        List<List<Integer>> tag = new ArrayList<>();
        tag.add(weeklytags);
        tag.add(monthlytags);

        return tag;
    }

    // 기록한 날짜 + 컨디션 나타내는 잔디 심기 차트 = 깃헙
    public List<Map<String,Object>> grassChart(org.springframework.security.core.userdetails.User principal) {
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("nothing")
        );

        List<Cards> cards = cardsRepository.findCardsByUser(user);
        List<Map<String,Object>> grassList = new ArrayList<>();

        for (int i = 0; i < cards.size(); i++){
            Map<String,Object> grass = new LinkedHashMap<>();
            LocalDate selectedAt = cards.get(i).getSelectedAt();
            String day = selectedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            Long conditions = cards.get(i).getConditions();
            grass.put("day",day);
            grass.put("value",conditions);
            grassList.add(grass);
        }

        return grassList;
    }

    // 이번주 수면시간 표
    public List<List<Integer>> weeklyTable(LocalDate today, org.springframework.security.core.userdetails.User principal) {
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("nothing")
        );

        //주간 데이터
        LocalDate weekly = today.minusDays(7L);
        List<Cards> weeklyCards = cardsRepository.findCardsBySelectedAtIsAfterAndUser(weekly, user); // 7일까지의 유저 카드 불러오기

        List<List<Integer>> weeklyTableList = new ArrayList<>();

        List<Integer> weeklyStartSleepList = new ArrayList<>();
        Integer weeklyStartSleep = 0;
        List<Integer> weeklyEndSleepList = new ArrayList<>();
        Integer weeklyEndSleep = 0;
        List<Integer> weeklyTotalSleepList = new ArrayList<>();
        Integer weeklyTotalSleep = 0;

        for (int i = 0; i < weeklyCards.size(); i++){
            Integer startSleepHour = weeklyCards.get(i).getStartSleep().getHour();
            Integer startSleepMinuite = weeklyCards.get(i).getStartSleep().getMinute();

            weeklyStartSleep += startSleepHour*60 + startSleepMinuite; // 주간 취침 시간 합(분)

            Integer endSleepHour = weeklyCards.get(i).getEndSleep().getHour();
            Integer endSleepMinuite = weeklyCards.get(i).getEndSleep().getMinute();

            weeklyEndSleep += endSleepHour*60 + endSleepMinuite; // 주간 기상 시간 합(분)

            Integer totalSleepHour = Math.toIntExact(weeklyCards.get(i).getTotalSleepHour());
            Integer totalSleepMinute = Math.toIntExact(weeklyCards.get(i).getTotalSleepMinute());

            weeklyTotalSleep += totalSleepHour*60 + totalSleepMinute; // 주간 수면 시간 합(분)
        }

        try {
            weeklyStartSleepList.add(weeklyStartSleep/weeklyCards.size()/60); // 주간 취침시간(분) -> 시간
            weeklyStartSleepList.add(weeklyStartSleep/weeklyCards.size()%60); // 주간 취침시간(분) -> 분
            weeklyTableList.add(weeklyStartSleepList);

            weeklyEndSleepList.add(weeklyEndSleep/weeklyCards.size()/60); // 주간 기상시간(분) -> 시간
            weeklyEndSleepList.add(weeklyEndSleep/weeklyCards.size()%60); // 주간 기상시간(분) -> 분
            weeklyTableList.add(weeklyEndSleepList);

            weeklyTotalSleepList.add(weeklyTotalSleep/weeklyCards.size()/60); // 주간 수면시간(분) -> 시간
            weeklyTotalSleepList.add(weeklyTotalSleep/weeklyCards.size()%60); // 주간 수면시간(분) -> 분
            weeklyTableList.add(weeklyTotalSleepList);

        }catch (ArithmeticException e){
            List<Integer> zero = new ArrayList<>();
            weeklyTableList.add(zero);
            weeklyTableList.add(zero);
            weeklyTableList.add(zero);
            weeklyTableList.add(yoursleeptimebyconditions(principal));
            return weeklyTableList;
        }

        weeklyTableList.add(yoursleeptimebyconditions(principal));

        return weeklyTableList;
    }



    //적정 수면 시간, 주간 수면 시간 비교
    public List<LineChartResponseDto> compareToSleeptime(LocalDate today,
                                                         org.springframework.security.core.userdetails.User principal) {

        User user = userRepository.findByUsername(SecurityUtil.getCurrentUsername()).orElseThrow(
                () -> new IllegalArgumentException("There is no user by that name.")
        );

        //today: 2021.05.09
        //aWeekAgo : 2021.05.03
        LocalDate aWeekAgo = today.minusDays(6);

        //오늘까지라는 기간 설정을 해야 해
        List<Cards> cardsForSevenDays = cardsRepository.findCardsBySelectedAtBetweenAndUser(aWeekAgo, today, user);
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

            Collections.sort(lineChart, (a, b) ->a.getDate().compareTo(b.getDate()));

        }
        return lineChart;


    }

    //오늘의 적정 수면시간 구하기
    public double adequateSleepTimeOfToday(LocalDate today, User user) {
        //오늘을 기준으로 어제까지 작성한 카드의 데이터가 전부 출력 되야 함.
        //5월 2일 이전에 쓴 컨디션이 3,4,5인 모든 카드들 조회
        List<Cards> cardsByToday = cardsRepository.findCardsByConditionsGreaterThanAndSelectedAtBeforeAndUserEquals(2L, today, user);
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
                    total += ((sleepHour4 * 60) + sleepMinute4) * 2;
                    System.out.println("total = " + total);
                    break;

                case 5:
                    count5 += 1;
                    Long sleepHour5 = card.getTotalSleepHour();
                    Long sleepMinute5 = card.getTotalSleepMinute();
                    total += ((sleepHour5 * 60) + sleepMinute5) * 3;
                    System.out.println("total = " + total);
                    break;
            }
        }//

        log.info("count3 = {}", count3);
        log.info("count4 = {}", count4);
        log.info("count5 = {}", count5);


        try {
            total /= count3 + (count4 * 2L) + (count5 * 3L);
            double adqSleeptime = Math.floor((total / 60.0) * 10) / 10;
            log.info("적정 수면시간 = {} ", adqSleeptime);

            return adqSleeptime;
        } catch (ArithmeticException e){
            return 0;
        }

    }

}