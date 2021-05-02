package project.sleepwell.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.sleepwell.domain.cards.Cards;
import project.sleepwell.domain.cards.CardsRepository;
import project.sleepwell.domain.user.User;
import project.sleepwell.domain.user.UserRepository;
import project.sleepwell.web.dto.CardsRequestDto;
import project.sleepwell.web.dto.CardsResponseDto;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CardsService {
    private final CardsRepository cardsRepository;
    private final UserRepository userRepository;

    //카드 만들기
    public Long createCard(CardsRequestDto requestDto, org.springframework.security.core.userdetails.User principal) {
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("nothing")
        );
        Cards card = new Cards(requestDto, user);
        cardsRepository.save(card);

        return card.getId();
    }

    //수정
    @Transactional
    public String update(LocalDate selectedAt, CardsRequestDto requestDto, org.springframework.security.core.userdetails.User principal){
        Cards cards = cardsRepository.findBySelectedAt(selectedAt).orElseThrow(
                () -> new IllegalArgumentException("{\"selectedAt\":"+selectedAt+"}")
        );
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("nothing")
        );

        cards.update(requestDto, user);

        return "ok";
    }

    //상세조회
    public CardsResponseDto findBySelectedAt(LocalDate selectedAt) {
        Cards entity = cardsRepository.findBySelectedAt(selectedAt).orElseThrow(
                () -> new IllegalArgumentException("{\"selectedAt\":"+selectedAt+"}")
        );
        return new CardsResponseDto(entity);
    }

    //전체조회
    public List<CardsResponseDto> findAll() {
        return cardsRepository.findAll().stream().map(CardsResponseDto::new)
                .collect(Collectors.toList());
    }

    //내가 작성한 캘린더(카드들) 다 조회하기
    public Map<String,Object> getMyCalendars(org.springframework.security.core.userdetails.User principal) {
        Map<String, Object> calendarInfo = new LinkedHashMap<>();

        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("nothing")
        );
        List<Cards> cards = user.getCards();

        calendarInfo.put("userId", user.getId());
        calendarInfo.put("cards", cards);

//        List<Cards> cards = cardRepository.findByUserId(user.getId());
//        List<Cards> cards = user.get().getCards();
        return calendarInfo;


    }


    //삭제
    @Transactional
    public String delete(LocalDate selectedAt) {
        Cards cards = cardsRepository.findBySelectedAt(selectedAt).orElseThrow(
                () -> new IllegalArgumentException("{\"selectedAt\":"+selectedAt+"}")
        );
        cardsRepository.delete(cards);
        return "ok";
    }
}
