package project.sleepwell.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.sleepwell.domain.cards.Cards;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@NoArgsConstructor
// View Layer와 DB Layer의 역할 분리를 철저하게 하는 것이 좋음
public class CardsRequestDto {
    private LocalTime startSleep;
    private LocalTime endSleep;
    private List<String> tag;
    private Long condition;
    private String memo;
    private LocalDate selectedAt;


    //save,update dto
    @Builder
    public CardsRequestDto(LocalTime startSleep, LocalTime endSleep, List<String> tag, Long condition, String memo, LocalDate selectedAt) {
        this.startSleep = startSleep;
        this.endSleep = endSleep;
        this.tag = tag;
        this.condition = condition;
        this.memo = memo;
        this.selectedAt = selectedAt;
    }

    public Cards toEntity() {
        return Cards.builder()
                .startSleep(startSleep)
                .endSleep(endSleep)
                .tag(tag)
                .condition(condition)
                .memo(memo)
                .selectedAt(selectedAt)
                .build();
    }

}
