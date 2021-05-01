package project.sleepwell.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.sleepwell.domain.cards.Cards;
import project.sleepwell.domain.cards.CardsRepository;
import project.sleepwell.web.dto.CardsRequestDto;
import project.sleepwell.web.dto.CardsResponseDto;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CardsService {
    private final CardsRepository cardsRepository;

    //게시
    @Transactional
    public String save(CardsRequestDto requestDto) {
        cardsRepository.save(requestDto.toEntity());
        return "ok";
    }

    //수정
    @Transactional
    public String update(LocalDate selectedAt, CardsRequestDto requestDto){
        Cards cards = cardsRepository.findBySelectedAt(selectedAt).orElseThrow(
                () -> new IllegalArgumentException("{\"selectedAt\":"+selectedAt+"}")
        );
        cards.update(requestDto.toEntity().getUser(), requestDto.getStartSleep(), requestDto.getEndSleep(),
                requestDto.getTag(), requestDto.getConditions(), requestDto.getMemo(), requestDto.getSelectedAt());

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
    public List<Cards> findByUserId(Long userId) {
        List<Cards> findAllCards = cardsRepository.findByUserId(userId);
//        return cardsRepository.findAll().stream().map(CardsResponseDto::new)
//                .collect(Collectors.toList());
        return findAllCards;
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
