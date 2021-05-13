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
    public List<Integer> yourSleepTimeByConditions(){
        User user = userRepository.findById(SecurityUtil.getCurrentUserId()).orElseThrow(
                () -> new IllegalArgumentException("There is no user.")
        );
        List<Cards> cards = cardsRepository.findCardsByConditionsGreaterThanAndUserEquals(2L,user); //컨디션이 2보다 큰 카드

        List<Long> sleepTime03 = new ArrayList<>(); //컨디션이 보통인 카드들의 수면 시간, 분을 담을 그릇
        List<Long> sleepTime04 = new ArrayList<>(); //컨디션이 좋음인 카드들의 수면 시간, 분을 담을 그릇
        List<Long> sleepTime05 = new ArrayList<>(); //컨디션이 매우좋음 카드들의 수면 시간, 분을 담을 그릇

        for(int i = 0; i < cards.size(); i++){ //카드의 개수만큼 반복
            if (cards.get(i).getConditions()==3){
                sleepTime03.add(cards.get(i).getTotalSleepHour());
                sleepTime03.add(cards.get(i).getTotalSleepMinute());
            }
            if (cards.get(i).getConditions()==4){
                sleepTime04.add(cards.get(i).getTotalSleepHour());
                sleepTime04.add(cards.get(i).getTotalSleepMinute());
            }
            if (cards.get(i).getConditions()==5){
                sleepTime05.add(cards.get(i).getTotalSleepHour());
                sleepTime05.add(cards.get(i).getTotalSleepMinute());
            }
        }
        // [수면시간, 분, 수면시간, 분, ... ]

        int totaltime = 0;
        for (int i = 0; i < sleepTime03.size(); i++){
            if(i%2==0){
                totaltime += sleepTime03.get(i)*60; // 수면시간은 짝수 인덱스 -> 분으로 변환 위해 60 곱
            }
            if(i%2!=0){
                totaltime += sleepTime03.get(i); // 수면 분은 홀수 인덱스
            }
        }
        for (int i = 0; i < sleepTime04.size(); i++){
            if(i%2==0){
                totaltime += sleepTime04.get(i)*60*2;
            }
            if(i%2!=0){
                totaltime += sleepTime04.get(i)*2;
            }
        }
        for (int i = 0; i < sleepTime05.size(); i++){
            if(i%2==0){
                totaltime += sleepTime05.get(i)*60*3;
            }
            if(i%2!=0){
                totaltime += sleepTime05.get(i)*3;
            }
        }

        // [3카드],[3카드],[3카드],[3카드],[3카드],[4카드],[4카드],[4카드],[5카드],[5카드],[5카드],[5카드],[5카드]
        // 3카드*5+4카드*3+5카드*5/1*5+2*3+3*5

        try {
            totaltime /= ((sleepTime03.size() / 2)+sleepTime04.size()+(3* sleepTime05.size() / 2));
        }catch (ArithmeticException e){
            List<Integer> zero = new ArrayList<>();
            return zero;
        }

        int yourSleepHour = totaltime / 60;
        int yourSleepMinute = totaltime % 60;

        List<Integer> yourSleepTime = new ArrayList<>();
        yourSleepTime.add(yourSleepHour);
        yourSleepTime.add(yourSleepMinute);

        return yourSleepTime;
    }

    //주간, 월간 태그 빈도수 -> 막대그래프
    public List<List<Integer>> tagBarChart(LocalDate today){
        User user = userRepository.findById(SecurityUtil.getCurrentUserId()).orElseThrow(
                () -> new IllegalArgumentException("There is no user.")
        );

        //주간 데이터
        LocalDate weekly = today.minusDays(7L);
        List<Cards> weeklyCards = cardsRepository.findCardsBySelectedAtIsAfterAndUser(weekly,user);
        List<Integer> weeklyTags= new ArrayList<>();

        Integer tagExerciseWeekly = 0;
        Integer tagDrinkWeekly = 0;
        Integer tagNightWeekly = 0;
        Integer tagMidNightSnackWeekly = 0;

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
                    tagMidNightSnackWeekly += 1;
                }
            }
        }

        weeklyTags.add(tagExerciseWeekly);
        weeklyTags.add(tagDrinkWeekly);
        weeklyTags.add(tagNightWeekly);
        weeklyTags.add(tagMidNightSnackWeekly);
        weeklyTags.add(7);

        // 월간 데이터
        LocalDate monthly = today.minusDays(30L);
        List<Cards> monthlyCards = cardsRepository.findCardsBySelectedAtIsAfterAndUser(monthly,user);
        List<Integer> monthlyTags= new ArrayList<>();

        Integer tagExerciseMonthly = 0;
        Integer tagDrinkMonthly = 0;
        Integer tagNightMonthly = 0;
        Integer tagMidNightSnackMonthly = 0;

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
                    tagMidNightSnackMonthly += 1;
                }
            }
        }
        monthlyTags.add(tagExerciseMonthly);
        monthlyTags.add(tagDrinkMonthly);
        monthlyTags.add(tagNightMonthly);
        monthlyTags.add(tagMidNightSnackMonthly);
        monthlyTags.add(30);

        List<List<Integer>> tagCounts = new ArrayList<>();
        tagCounts.add(weeklyTags);
        tagCounts.add(monthlyTags);

        return tagCounts;
    }

    // 기록한 날짜 + 컨디션 나타내는 잔디 심기 차트
    public List<Map<String,Object>> grassChart() {
        User user = userRepository.findById(SecurityUtil.getCurrentUserId()).orElseThrow(
                () -> new IllegalArgumentException("There is no user.")
        );

        List<Cards> cards = cardsRepository.findCardsByUser(user);
        List<Map<String,Object>> dayOfConditionList = new ArrayList<>();

        for (int i = 0; i < cards.size(); i++){
            Map<String,Object> dayOfCondition = new LinkedHashMap<>();
            LocalDate selectedAt = cards.get(i).getSelectedAt();
            String day = selectedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            Long conditions = cards.get(i).getConditions();
            dayOfCondition.put("day",day);
            dayOfCondition.put("value",conditions);
            dayOfConditionList.add(dayOfCondition);
        }
        // 데이터 형식 - [{"day": 2020-02-02, "value": 3},{...},...]

        return dayOfConditionList;
    }

    // 이번주 수면시간 표
    public List<List<Integer>> weeklyTable(LocalDate today) {
        User user = userRepository.findById(SecurityUtil.getCurrentUserId()).orElseThrow(
                () -> new IllegalArgumentException("There is no user.")
        );

        //주간 데이터
        LocalDate aWeekAgo = today.minusDays(7L);
        List<Cards> weeklyCards = cardsRepository.findCardsBySelectedAtIsAfterAndUser(aWeekAgo, user); // 7일까지의 유저 카드 불러오기

        //리턴할 그릇
        List<List<Integer>> weeklyTable = new ArrayList<>();

        //항목별 주간 데이터
        List<Integer> weeklyStartSleep = new ArrayList<>();
        List<Integer> weeklyEndSleep = new ArrayList<>();
        List<Integer> weeklyTotalSleep = new ArrayList<>();

        //해당 날짜 데이터
        Integer StartSleep = 0;
        Integer EndSleep = 0;
        Integer TotalSleep = 0;

        for (int i = 0; i < weeklyCards.size(); i++){
            Integer startSleepHour = weeklyCards.get(i).getStartSleep().getHour();
            Integer startSleepMinute = weeklyCards.get(i).getStartSleep().getMinute();

            StartSleep += startSleepHour*60 + startSleepMinute; // 주간 취침 시간 합(분)

            Integer endSleepHour = weeklyCards.get(i).getEndSleep().getHour();
            Integer endSleepMinute = weeklyCards.get(i).getEndSleep().getMinute();

            EndSleep += endSleepHour*60 + endSleepMinute; // 주간 기상 시간 합(분)

            Integer totalSleepHour = Math.toIntExact(weeklyCards.get(i).getTotalSleepHour());
            Integer totalSleepMinute = Math.toIntExact(weeklyCards.get(i).getTotalSleepMinute());

            TotalSleep += totalSleepHour*60 + totalSleepMinute; // 주간 수면 시간 합(분)
        }

        try {
            weeklyStartSleep.add(StartSleep/weeklyCards.size()/60); // 주간 취침시간(분) -> 시간
            weeklyStartSleep.add(StartSleep/weeklyCards.size()%60); // 주간 취침시간(분) -> 분
            weeklyTable.add(weeklyStartSleep);

            weeklyEndSleep.add(EndSleep/weeklyCards.size()/60); // 주간 기상시간(분) -> 시간
            weeklyEndSleep.add(EndSleep/weeklyCards.size()%60); // 주간 기상시간(분) -> 분
            weeklyTable.add(weeklyEndSleep);

            weeklyTotalSleep.add(TotalSleep/weeklyCards.size()/60); // 주간 수면시간(분) -> 시간
            weeklyTotalSleep.add(TotalSleep/weeklyCards.size()%60); // 주간 수면시간(분) -> 분
            weeklyTable.add(weeklyTotalSleep);
            // [[평균 취침시간,분],[평균 기상시간,분],[평균 수면시간,분]]
        }catch (ArithmeticException e){
            List<Integer> zero = new ArrayList<>();
            weeklyTable.add(zero);
            weeklyTable.add(zero);
            weeklyTable.add(zero);
            weeklyTable.add(yourSleepTimeByConditions());
            return weeklyTable;
        }

        weeklyTable.add(yourSleepTimeByConditions());
        // [[주간 평균 취침시간,분],[주간 평균 기상시간,분],[주간 평균 수면시간,분],[적정 수면시간, 분]]

        return weeklyTable;
    }



    //적정 수면 시간, 총 수면 시간 비교 (주간)
    public List<LineChartResponseDto> compareToSleeptime(LocalDate today) {

        User user = userRepository.findById(SecurityUtil.getCurrentUserId()).orElseThrow(
                () -> new IllegalArgumentException("There is no user by that name.")
        );

        LocalDate aWeekAgo = today.minusDays(6);

        List<Cards> cardsForSevenDays = cardsRepository.findCardsBySelectedAtBetweenAndUser(aWeekAgo, today, user);
        List<LineChartResponseDto> lineChart = new ArrayList<>();


        for (Cards card : cardsForSevenDays) {
            LocalDate date = card.getSelectedAt();
            Long sleepHour = card.getTotalSleepHour();
            Long sleepMinute = card.getTotalSleepMinute();

            long convertToMin = (sleepHour * 60) + sleepMinute;
            double totalTime = Math.floor((convertToMin / 60.0) * 10) / 10;

            double adequateSleepTime = adequateSleepTimeOfToday(date, user);

            LineChartResponseDto lineChartResponseDto = new LineChartResponseDto(date, totalTime, adequateSleepTime);
            lineChart.add(lineChartResponseDto);

            Collections.sort(lineChart, (a, b) ->a.getDate().compareTo(b.getDate()));

        }
        return lineChart;


    }

    //오늘의 적정 수면시간
    public double adequateSleepTimeOfToday(LocalDate today, User user) {
        List<Cards> cardsByToday = cardsRepository.findCardsByConditionsGreaterThanAndSelectedAtBeforeAndUserEquals(2L, today, user);

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
                    break;

                case 4:
                    count4 += 1;
                    Long sleepHour4 = card.getTotalSleepHour();
                    Long sleepMinute4 = card.getTotalSleepMinute();
                    total += ((sleepHour4 * 60) + sleepMinute4) * 2;
                    break;

                case 5:
                    count5 += 1;
                    Long sleepHour5 = card.getTotalSleepHour();
                    Long sleepMinute5 = card.getTotalSleepMinute();
                    total += ((sleepHour5 * 60) + sleepMinute5) * 3;
                    break;
            }
        } //for

        try {

            total /= count3 + (count4 * 2L) + (count5 * 3L);
            double adqSleeptime = Math.floor((total / 60.0) * 10) / 10;
            return adqSleeptime;

        } catch (ArithmeticException e){
            return 0;
        }

    } //adequateSleepTimeOfToday


}