package project.sleepwell.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
// View Layer와 DB Layer의 역할 분리를 철저하게 하는 것이 좋음
public class CardsRequestDto {
    @JsonFormat(pattern = "HH:mm")//@JsonFormat -> 날짜가 배열로 출력되는 것을 지정된 패턴으로 출력하게 함
    private LocalTime startSleep;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endSleep;
    private List<String> tag;
    private Long conditions;
    private String memo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate selectedAt;

    public List<Long> totalSleep(LocalTime startSleep, LocalTime endSleep){
        List<Long> myTotalSleep = new ArrayList<>();
        //case01 - 둘다 양수
        Long totalSleepMinute = ChronoUnit.MINUTES.between(this.startSleep, this.endSleep)%60;
        Long totalSleepHour = ChronoUnit.MINUTES.between(this.startSleep, this.endSleep)/60;
        //case02 - 시간 양수, 분 음수
        if (totalSleepHour >= 0 & totalSleepMinute < 0) {
            totalSleepHour = totalSleepHour - 1L;
            totalSleepMinute = totalSleepMinute + 60L;
        }
        //case03 - 시간 음수, 분 양수
        if (totalSleepHour < 0 & totalSleepMinute >= 0) {
            totalSleepHour = totalSleepHour + 24L;
        }
        //case04 - 둘다 음수
        if (totalSleepHour < 0 & totalSleepMinute < 0) {
            totalSleepHour = totalSleepHour + 23L;
            totalSleepMinute = totalSleepMinute + 60L;
        }
        myTotalSleep.add(totalSleepHour);
        myTotalSleep.add(totalSleepMinute);
        return myTotalSleep;
    }

}
