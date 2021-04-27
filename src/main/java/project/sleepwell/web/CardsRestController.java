package project.sleepwell.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import project.sleepwell.service.cards.CardsService;
import project.sleepwell.web.dto.CardsRequestDto;
import project.sleepwell.web.dto.CardsResponseDto;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CardsRestController {

    private final CardsService cardsService;

    //전체 조회
    @GetMapping("/cards")
    public List<CardsResponseDto> findAllDesc(){
        return cardsService.findAllDesc();
    }

    //게시
    @PostMapping("/cards")
    public String save(@RequestBody CardsRequestDto requestDto) {
        return cardsService.save(requestDto);
    }

    //수정
    @PutMapping("/cards/{id}")
    public String update(@PathVariable Long id, @RequestBody CardsRequestDto requestDto) {
        return cardsService.update(id,requestDto);
    }

    //상세 조회
    @GetMapping("/cards/{id}")
    public CardsResponseDto findById(@PathVariable Long id) {
        return cardsService.findById(id);
    }

    //삭제
    @DeleteMapping("/cards/{id}")
    public String delete(@PathVariable Long id) {
        return cardsService.delete(id);
    }

}
