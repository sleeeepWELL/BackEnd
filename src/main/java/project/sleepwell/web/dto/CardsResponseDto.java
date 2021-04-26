package project.sleepwell.web.dto;

import lombok.Getter;
import project.sleepwell.domain.cards.Cards;
import java.time.LocalDateTime;

@Getter
public class CardsResponseDto {
    private Long id;
    private LocalDateTime startSleep;
    private LocalDateTime endSleep;
    private Long totalSleep;
    private String tag;
    private Long condition;
    private String memo;

    public CardsResponseDto(Cards entity){
        this.id = entity.getId();
        this.startSleep = entity.getStartSleep();
        this.endSleep = entity.getEndSleep();
        this.totalSleep = entity.getTotalSleep();
        this.tag = entity.getTag();
        this.condition = entity.getCondition();
        this.memo = entity.getMemo();
    }
}
