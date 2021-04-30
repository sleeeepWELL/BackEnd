package project.sleepwell.domain.cards;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest
class CardsRepositoryTest {
    @Autowired
    CardsRepository cardsRepository;
    @AfterEach//junit4의 After와 동일 - 단위 테스트가 끝날 떄 마다 수행되는 메서드 지
    public void cleanup() {
        cardsRepository.deleteAll();
    }
    @Test
    @Transactional
    @DisplayName("정상 케이스")
    public void 게시글저장_불러오기() {
        //given
        LocalTime startSleep = LocalTime.of(22,0,0);
        LocalTime endSleep = LocalTime.of(9,0,0);
        List<String> tag = Arrays.asList("음주");
//        List<String> tag = new ArrayList<>();
//        tag.add("운동");
        Long condition = 1L;
        String memo = "오늘은 즐거웠다";
        LocalDate selectedAt = LocalDate.of(2021,4,17);
        cardsRepository.save(Cards.builder()//insert/update쿼리 실행
                .startSleep(startSleep)
                .endSleep(endSleep)
                .tag(tag)
                .condition(condition)
                .memo(memo)
                .selectedAt(selectedAt)
                .build());
        //when
        List<Cards> cardsList = cardsRepository.findAll();
        //then
        Cards cards = cardsList.get(0);
        assertEquals(startSleep, cards.getStartSleep());
        assertEquals(endSleep, cards.getEndSleep());
        assertEquals(tag, cards.getTag());
        assertEquals(condition, cards.getCondition());
        assertEquals(memo, cards.getMemo());
        assertEquals(selectedAt, cards.getSelectedAt());
    }
}