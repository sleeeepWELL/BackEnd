package project.sleepwell.web.dto;

import lombok.Getter;
import project.sleepwell.domain.cards.Cards;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CardsResponseDto {
    private Long id;
    private LocalDateTime startSleep;
    private LocalDateTime endSleep;
    private Long totalSleep;
    private List<String> tag;
    private Long condition;
    private String memo;
    private LocalDate createdAt;

    public CardsResponseDto(Cards entity){
        this.id = entity.getId();
        this.startSleep = entity.getStartSleep();
        this.endSleep = entity.getEndSleep();
        this.totalSleep = entity.getTotalSleep();
        this.tag = entity.getTag();
        this.condition = entity.getCondition();
        this.memo = entity.getMemo();
        this.createdAt = entity.getCreatedAt();
    }
}
