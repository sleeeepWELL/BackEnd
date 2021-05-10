package project.sleepwell.analisys;


import org.springframework.data.jpa.repository.JpaRepository;
import project.sleepwell.domain.cards.Cards;
import project.sleepwell.domain.user.User;

import java.time.LocalDate;
import java.util.List;

public interface LineChartRepository extends JpaRepository<Cards, Long> {

    //한 주간의 카드 조회. 최대 카드 갯수 : 7개
    List<Cards> findCardsBySelectedAtBetweenAndUser(LocalDate start, LocalDate end, User user);

    List<Cards> findCardsByConditionsGreaterThanAndSelectedAtBeforeAndUserEquals(Long conNum, LocalDate today, User user);



}
