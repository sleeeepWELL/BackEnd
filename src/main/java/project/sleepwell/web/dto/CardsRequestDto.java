package project.sleepwell.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.sleepwell.domain.cards.Cards;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
// View Layer와 DB Layer의 역할 분리를 철저하게 하는 것이 좋음
public class CardsRequestDto {
    private LocalDateTime startSleep;
    private LocalDateTime endSleep;
    private Long totalSleep;
    private String tag;
    private Long condition;
    private String memo;

    //save,update dto
    @Builder
    public CardsRequestDto(LocalDateTime startSleep, LocalDateTime endSleep, Long totalSleep, String tag, Long condition, String memo) {
        this.startSleep = startSleep;
        this.endSleep = endSleep;
        this.totalSleep = totalSleep;
        this.tag = tag;
        this.condition = condition;
        this.memo = memo;
    }

    public Cards toEntity() {
        return Cards.builder()
                .startSleep(startSleep)
                .endSleep(endSleep)
                .totalSleep(totalSleep)
                .tag(tag)
                .condition(condition)
                .memo(memo)
                .build();
    }

}
