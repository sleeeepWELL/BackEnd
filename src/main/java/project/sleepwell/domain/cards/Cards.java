package project.sleepwell.domain.cards;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(nullable = false)
    private LocalTime startSleep;

    @Column(nullable = false)
    private LocalTime endSleep;

    @Column
    private Long totalSleep;

    @ElementCollection
    private List<String> tag;

    @Column(nullable = false)
    private Long condition;

    @Column
    private String memo;

    @Column
    private LocalDate selectedAt;

    // 생성자 대신에 @Builder를 통해 빌더 클래스를 사용 -> 지금 채워야할 필드의 역할이 무엇인지 정확히 지정
    @Builder
    public Cards(LocalTime startSleep, LocalTime endSleep, List<String> tag , Long condition, String memo, LocalDate selectedAt) {
        this.startSleep = startSleep;
        this.endSleep = endSleep;
        this.totalSleep = ChronoUnit.MINUTES.between(startSleep, endSleep); // 기상시간 - 취침시간
        if (this.totalSleep < 0) { // 음수라면
            this.totalSleep = this.totalSleep + 1440L; //1440 더함
        }
        this.selectedAt = selectedAt;
        this.condition = condition;
        this.tag = tag;
        this.memo = memo;
    }

    public void update(LocalTime startSleep, LocalTime endSleep, List<String> tag ,Long condition, String memo, LocalDate selectedAt){
        this.startSleep = startSleep;
        this.endSleep = endSleep;
        this.totalSleep = ChronoUnit.MINUTES.between(startSleep, endSleep); // 기상시간 - 취침시간
        if (this.totalSleep < 0) { // 음수라면
            this.totalSleep = this.totalSleep + 1440L; //1440 더함
        }
        this.selectedAt = selectedAt;
        this.condition = condition;
        this.tag = tag;
        this.memo = memo;
    }
}
