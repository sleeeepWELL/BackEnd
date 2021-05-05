package project.sleepwell.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.sleepwell.domain.cards.Cards;
import project.sleepwell.domain.cards.CardsRepository;
import project.sleepwell.domain.user.User;
import project.sleepwell.domain.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChartService {
    private final CardsRepository cardsRepository;
    private final UserRepository userRepository;

    //conditons이 2보다 큰 카드에서 sleeptime을 추출하여 평균 계산
    public List<Integer> yoursleeptimebyconditions(org.springframework.security.core.userdetails.User principal){
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("nothing")
        );
        List<Cards> cards = cardsRepository.findCardsByConditionsGreaterThanAndUserEquals(2L,user);

        List<Long> times = new ArrayList<>();
        for(int i = 0; i < cards.size(); i++){
            times.add(cards.get(i).getTotalSleepHour());
            times.add(cards.get(i).getTotalSleepMinute());
        }
        int totaltime = 0;
        for (int i = 0; i < times.size(); i++){
            if(i%2==0){
                totaltime += times.get(i)*60;
            }
            if(i%2!=0){
                totaltime += times.get(i);
            }
        }

        try {
            totaltime /= (times.size() / 2);
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

}
