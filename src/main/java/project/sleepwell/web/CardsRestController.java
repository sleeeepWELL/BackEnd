package project.sleepwell.web;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import project.sleepwell.service.cards.CardsService;
import project.sleepwell.web.dto.CardsRequestDto;
import project.sleepwell.web.dto.CardsResponseDto;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class CardsRestController {

    private final CardsService cardsService;

    //전체 조회
    @GetMapping("/calendars")
    public List<CardsResponseDto> findAll(){
        return cardsService.findAll();
    }

    //게시
    @PostMapping("/cards")
    public String save(@RequestBody CardsRequestDto requestDto) {
        return cardsService.save(requestDto);
    }

    //수정
    @PutMapping("/cards/{selectedAt}")
    public String update(@PathVariable("selectedAt") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate selectedAt, @RequestBody CardsRequestDto requestDto) {
        return cardsService.update(selectedAt,requestDto);
    }

    //상세 조회
    @GetMapping("/cards/{selectedAt}")
    public CardsResponseDto findBySelectedAt(@PathVariable("selectedAt") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate selectedAt) {
        return cardsService.findBySelectedAt(selectedAt);
    }

    //삭제
    @DeleteMapping("/cards/{selectedAt}")
    public String delete(@PathVariable("selectedAt") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate selectedAt) {
        return cardsService.delete(selectedAt);
    }

}
