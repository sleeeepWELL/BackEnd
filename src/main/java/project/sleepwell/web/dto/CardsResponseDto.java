package project.sleepwell.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import project.sleepwell.domain.cards.Cards;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
public class CardsResponseDto {
    private Long id;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startSleep;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endSleep;
    private Long totalSleepHour;
    private Long totalSleepMinute;
    private List<String> tag;
    private Long condition;
    private String memo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate selectedAt;

    public CardsResponseDto(Cards entity){
        this.id = entity.getId();
        this.startSleep = entity.getStartSleep();
        this.endSleep = entity.getEndSleep();
        this.totalSleepHour = entity.getTotalSleepHour();
        this.totalSleepMinute = entity.getTotalSleepMinute();
        this.tag = entity.getTag();
        this.condition = entity.getCondition();
        this.memo = entity.getMemo();
        this.selectedAt = entity.getSelectedAt();
    }
}
