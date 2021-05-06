package project.sleepwell.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.sleepwell.domain.cards.Cards;
import project.sleepwell.domain.cards.CardsRepository;
import project.sleepwell.domain.user.User;
import project.sleepwell.domain.user.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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


    public List<List<Integer>> tagbarchart(LocalDate today, org.springframework.security.core.userdetails.User principal){
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("nothing")
        );

        LocalDate weekly = today.minusDays(7L);
        LocalDate monthly = today.minusDays(30L);
        List<Cards> weeklyCards = cardsRepository.findCardsBySelectedAtIsAfterAndUser(weekly,user);
        List<Cards> monthlyCards = cardsRepository.findCardsBySelectedAtIsAfterAndUser(monthly,user);

        List<Integer> weeklytags= new ArrayList<>();
        List<Integer> monthlytags= new ArrayList<>();
        List<List<Integer>> tag = new ArrayList<>();


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

        tag.add(weeklytags);
        tag.add(monthlytags);

        return tag;
    }

}
