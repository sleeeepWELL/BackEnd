package project.sleepwell.domain.cards;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.sleepwell.domain.user.User;
import project.sleepwell.web.dto.CardsRequestDto;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Cards{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    private User user;

    @Column(nullable = false)
    private LocalTime startSleep;

    @Column(nullable = false)
    private LocalTime endSleep;

    @Column
    private Long totalSleepHour;

    @Column
    private Long totalSleepMinute;

    @ElementCollection
    private List<String> tag;

    @Column(nullable = false)
    private Long conditions;

    @Column
    private String memo;

    @Column
    private LocalDate selectedAt;

    // 생성자 대신에 @Builder를 통해 빌더 클래스를 사용 -> 지금 채워야할 필드의 역할이 무엇인지 정확히 지정
    @Builder
    public Cards(User user, LocalTime startSleep, LocalTime endSleep, List<String> tag , Long conditions, String memo, LocalDate selectedAt) {
        this.user = user;
        this.startSleep = startSleep;
        this.endSleep = endSleep;
        this.totalSleepMinute = ChronoUnit.MINUTES.between(startSleep, endSleep)%60; // 기상시간 - 취침시간 % 60
        if (this.totalSleepMinute < 0) { // 음수이면
            this.totalSleepMinute = this.totalSleepMinute + 60L;
        }
        this.totalSleepHour = ChronoUnit.MINUTES.between(startSleep, endSleep)/60; // 기상시간 - 취침시간 / 60
        if (this.totalSleepHour <= 0) { // 음수, 0이면
            this.totalSleepHour = this.totalSleepHour + 23L;
        }
        this.selectedAt = selectedAt;
        this.conditions = conditions;
        this.tag = tag;
        this.memo = memo;
    }

    public void update(User user, LocalTime startSleep, LocalTime endSleep, List<String> tag ,Long conditions, String memo, LocalDate selectedAt){
        this.user = user;
        this.startSleep = startSleep;
        this.endSleep = endSleep;
        this.totalSleepMinute = ChronoUnit.MINUTES.between(startSleep, endSleep)%60;
        if (this.totalSleepMinute < 0) {
            this.totalSleepMinute = this.totalSleepMinute + 60L;
        }
        this.totalSleepHour = ChronoUnit.MINUTES.between(startSleep, endSleep)/60;
        if (this.totalSleepHour <= 0) {
            this.totalSleepHour = this.totalSleepHour + 23L;
        }
        this.selectedAt = selectedAt;
        this.conditions = conditions;
        this.tag = tag;
        this.memo = memo;
    }



    //테스트 좀 해보겠습니다.
    public Cards(CardsRequestDto requestDto, User user) {
        this.startSleep = requestDto.getStartSleep();
        this.endSleep = requestDto.getEndSleep();
        this.tag = requestDto.getTag();
        this.conditions = requestDto.getConditions();
        this.memo = requestDto.getMemo();
        this.selectedAt = requestDto.getSelectedAt();
        this.user = user;
    }

}
