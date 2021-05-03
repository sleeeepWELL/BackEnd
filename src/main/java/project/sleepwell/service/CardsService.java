package project.sleepwell.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.sleepwell.domain.cards.Cards;
import project.sleepwell.domain.cards.CardsRepository;
import project.sleepwell.domain.user.User;
import project.sleepwell.domain.user.UserRepository;
import project.sleepwell.web.dto.CardsRequestDto;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

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
    public Cards findBySelectedAt(LocalDate selectedAt, org.springframework.security.core.userdetails.User principal) {
        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("nothing")
        );
        Cards cards= cardsRepository.findCardsBySelectedAtEqualsAndUser(selectedAt, user);
        if(cards==null){
            throw new IllegalArgumentException("{\"selectedAt\":"+selectedAt+"}");
        }
        return cards;
    }

    //전체조회
    public List<Cards> findAll() {
        return cardsRepository.findAll();
    }

    //내가 작성한 캘린더(카드들) 다 조회하기
    public List<Cards> getMyCalendars(org.springframework.security.core.userdetails.User principal) {

        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("nothing")
        );

        return cardsRepository.findCardsByUser(user);
    }

    //삭제
    @Transactional
    public String delete(LocalDate selectedAt, org.springframework.security.core.userdetails.User principal) {

        User user = userRepository.findByUsername(principal.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("nothing")
        );
        cardsRepository.deleteCardsBySelectedAtEqualsAndUser(selectedAt,user);
        return "ok";
    }
}
