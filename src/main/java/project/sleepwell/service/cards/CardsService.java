package project.sleepwell.service.cards;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.sleepwell.domain.cards.Cards;
import project.sleepwell.domain.cards.CardsRepository;
import project.sleepwell.web.dto.CardsRequestDto;
import project.sleepwell.web.dto.CardsResponseDto;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
                () -> new IllegalArgumentException("해당 게시글이 없습니다. 날짜=" + selectedAt)
        );
        cards.update(requestDto.getStartSleep(), requestDto.getEndSleep(),
                requestDto.getTag(),requestDto.getCondition(), requestDto.getMemo(), requestDto.getSelectedAt());

        return "ok";
    }

    //상세조회
    public CardsResponseDto findBySelectedAt(LocalDate selectedAt) {
        Cards entity = cardsRepository.findBySelectedAt(selectedAt).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 없습니다. 날짜=" + selectedAt)
        );
        return new CardsResponseDto(entity);
    }

    //전체조회
    public List<CardsResponseDto> findAllDesc() {
        return cardsRepository.findAllDesc().stream().map(CardsResponseDto::new)
                .collect(Collectors.toList());
    }

    //삭제
    @Transactional
    public String delete(LocalDate selectedAt) {
        Cards cards = cardsRepository.findBySelectedAt(selectedAt).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 없습니다. 날짜=" + selectedAt)
        );
        cardsRepository.delete(cards);
        return "ok";
    }
}
