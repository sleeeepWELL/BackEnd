package project.sleepwell.domain.cards;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.sleepwell.domain.user.User;
import project.sleepwell.web.dto.CardsRequestDto;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
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

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate selectedAt;

    // 글 작성 생성자
    public Cards(CardsRequestDto requestDto, User user) {
        this.user = user;
        this.startSleep = requestDto.getStartSleep();
        this.endSleep = requestDto.getEndSleep();
        this.totalSleepHour = requestDto.totalSleep(this.startSleep, this.endSleep).get(0);
        this.totalSleepMinute = requestDto.totalSleep(this.startSleep, this.endSleep).get(1);
        this.tag = requestDto.getTag();
        this.conditions = requestDto.getConditions();
        this.memo = requestDto.getMemo();
        this.selectedAt = requestDto.getSelectedAt();
    }

    //수정 생성자
    public void update(CardsRequestDto requestDto, User user){
        this.user = user;
        this.startSleep = requestDto.getStartSleep();
        this.endSleep = requestDto.getEndSleep();
        this.totalSleepHour = requestDto.totalSleep(this.startSleep, this.endSleep).get(0);
        this.totalSleepMinute = requestDto.totalSleep(this.startSleep, this.endSleep).get(1);
        this.selectedAt = requestDto.getSelectedAt();
        this.conditions = requestDto.getConditions();
        this.tag = requestDto.getTag();
        this.memo = requestDto.getMemo();
    }

}
