package project.sleepwell.service.cards;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.sleepwell.domain.cards.Cards;
import project.sleepwell.domain.cards.CardsRepository;
import project.sleepwell.web.dto.CardsRequestDto;
import project.sleepwell.web.dto.CardsResponseDto;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class CardsService {
    private final CardsRepository cardsRepository;

    @Transactional
    public String save(CardsRequestDto requestDto) {
        cardsRepository.save(requestDto.toEntity()).getId();
        return "ok";
    }

    @Transactional
    public String update(Long id, CardsRequestDto requestDto){
        Cards cards = cardsRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id)
        );
        cards.update(requestDto.getStartSleep(), requestDto.getEndSleep(), requestDto.getTotalSleep(),
                requestDto.getTag(),requestDto.getCondition(), requestDto.getMemo());

        return "ok";
    }

    public CardsResponseDto findById (Long id) {
        Cards entity = cardsRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id)
        );
        return new CardsResponseDto(entity);
    }
}
