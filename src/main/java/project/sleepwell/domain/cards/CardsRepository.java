package project.sleepwell.domain.cards;

import org.springframework.data.jpa.repository.JpaRepository;
import project.sleepwell.domain.user.User;

import java.time.LocalDate;
import java.util.List;


// Entity Class 인 Cards 와 같은 위치에 놓은 이유는 Entity 클래스는 기본 Repository 없이는 제대로 된 역할을 할 수 없기 때문
// 확장하게되어 도메인 별 프로젝트를 분리해야 할 때가 오면 Entity 와 Repository 는 같이 움직어야 하므로 도메인 패키지에서 함께 관리
public interface CardsRepository extends JpaRepository<Cards, Long> { // <Entity Class, PK type> -> 기본적인 CRUD 메서드 자동으로 생성

    List<Cards> findAll();

    Cards findCardsBySelectedAtEqualsAndUser(LocalDate selectedAt, User user);

    List<Cards> findCardsByUser(User user);

    String deleteCardsBySelectedAtEqualsAndUser(LocalDate selectedAt, User user);

    List<Cards> findCardsByConditionsGreaterThanAndUserEquals(Long conditions, User user);

}
