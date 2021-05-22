package project.sleepwell.domain.cards;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.sleepwell.domain.Timestamped;
import project.sleepwell.domain.user.User;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Cards extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    private User user;

    @Column(nullable = false)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startSleep;

    @Column(nullable = false)
    @JsonFormat(pattern = "HH:mm")
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

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate selectedAt;

    // 생성자 대신에 @Builder를 통해 빌더 클래스를 사용 -> 지금 채워야할 필드의 역할이 무엇인지 정확히 지정
    @Builder
    public Cards(LocalTime startSleep, LocalTime endSleep, List<String> tag , Long conditions, String memo, LocalDate selectedAt, User user) {
        this.startSleep = startSleep;
        this.endSleep = endSleep;
        //case01 - 둘다 양수
        this.totalSleepMinute = ChronoUnit.MINUTES.between(this.startSleep, this.endSleep)%60;
        this.totalSleepHour = ChronoUnit.MINUTES.between(this.startSleep, this.endSleep)/60;
        //case02 - 시간 양수, 분 음수
        if (this.totalSleepHour >= 0 & this.totalSleepMinute < 0) {
            this.totalSleepHour = this.totalSleepHour - 1L;
            this.totalSleepMinute = this.totalSleepMinute + 60L;
        }
        //case03 - 시간 음수, 분 양수
        if (this.totalSleepHour < 0 & this.totalSleepMinute >= 0) {
            this.totalSleepHour = this.totalSleepHour + 24L;
        }
        //case04 - 둘다 음수
        if (this.totalSleepHour < 0 & this.totalSleepMinute < 0) {
            this.totalSleepHour = this.totalSleepHour + 23L;
            this.totalSleepMinute = this.totalSleepMinute + 60L;
        }
        this.selectedAt = selectedAt;
        this.conditions = conditions;
        this.tag = tag;
        this.memo = memo;
        this.user = user;
    }

    //업데이트 시에 필요한 애들 명시해준 메서드
    public void update(LocalTime startSleep, LocalTime endSleep, List<String> tag ,Long conditions, String memo){
        this.startSleep = startSleep;
        this.endSleep = endSleep;
        //case01 - 둘다 양수
        this.totalSleepMinute = ChronoUnit.MINUTES.between(this.startSleep, this.endSleep)%60;
        this.totalSleepHour = ChronoUnit.MINUTES.between(this.startSleep, this.endSleep)/60;
        //case02 - 시간 양수, 분 음수
        if (this.totalSleepHour >= 0 & this.totalSleepMinute < 0) {
            this.totalSleepHour = this.totalSleepHour - 1L;
            this.totalSleepMinute = this.totalSleepMinute + 60L;
        }
        //case03 - 시간 음수, 분 양수
        if (this.totalSleepHour < 0 & this.totalSleepMinute >= 0) {
            this.totalSleepHour = this.totalSleepHour + 24L;
        }
        //case04 - 둘다 음수
        if (this.totalSleepHour < 0 & this.totalSleepMinute < 0) {
            this.totalSleepHour = this.totalSleepHour + 23L;
            this.totalSleepMinute = this.totalSleepMinute + 60L;
        }
        this.conditions = conditions;
        this.tag = tag;
        this.memo = memo;
    }

}
