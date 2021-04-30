package project.sleepwell.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "HH:mm")//@JsonFormat -> 날짜가 배열로 출력되는 것을 지정된 패턴으로 출력하게 함
    private LocalTime startSleep;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endSleep;
    private List<String> tag;
    private Long condition;
    private String memo;
    @JsonFormat(pattern = "yyyy-MM-dd")
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
