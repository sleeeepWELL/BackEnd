package project.sleepwell.domain.cards;

import org.springframework.data.jpa.repository.JpaRepository;
import project.sleepwell.domain.user.User;

import java.time.LocalDate;
import java.util.List;


// Entity Class 인 Cards 와 같은 위치에 놓은 이유는 Entity 클래스는 기본 Repository 없이는 제대로 된 역할을 할 수 없기 때문
// 확장하게되어 도메인 별 프로젝트를 분리해야 할 때가 오면 Entity 와 Repository 는 같이 움직어야 하므로 도메인 패키지에서 함께 관리
public interface CardsRepository extends JpaRepository<Cards, Long> { // <Entity Class, PK type> -> 기본적인 CRUD 메서드 자동으로 생성

    // 모두 찾기 - 현재 안씀
    List<Cards> findAll();

    // 상세 조회때 쓰는 유저별 selectedAt에 해당하는 카드 조회
    Cards findCardsBySelectedAtEqualsAndUser(LocalDate selectedAt, User user);

    // 유저별 카드 전체 조회
    List<Cards> findCardsByUser(User user);

    // 유저별 카드 삭제
    String deleteCardsBySelectedAtEqualsAndUser(LocalDate selectedAt, User user);

    // 유저별 적정 수면시간 구할 때 쓰는 카드 조회
    List<Cards> findCardsByConditionsGreaterThanAndUserEquals(Long conditions, User user);

    // 유저별 태그의 빈도수 조회
    List<Cards> findCardsBySelectedAtIsAfterAndUser(LocalDate today, User user);


    //한 주간의 카드 조회. 최대 카드 갯수 : 7개
    List<Cards> findCardsBySelectedAtBetweenAndUser(LocalDate start, LocalDate end, User user);

    List<Cards> findCardsByConditionsGreaterThanAndSelectedAtBeforeAndUserEquals(Long conNum, LocalDate today, User user);

}
