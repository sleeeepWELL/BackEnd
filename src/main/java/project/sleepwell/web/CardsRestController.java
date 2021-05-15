package project.sleepwell.web;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import project.sleepwell.domain.cards.Cards;
import project.sleepwell.service.CardsService;
import project.sleepwell.util.LogExecutionTime;
import project.sleepwell.web.dto.CardsRequestDto;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class CardsRestController {

    @ExceptionHandler(IllegalArgumentException.class)
    public String exceptionHandler(Exception e){
        return e.getMessage();
    }

    private final CardsService cardsService;

//    //전체 조회(관리자용..? 모든 카드 조회 - 혹시 몰라서 남겨둠.)
//    @GetMapping("/allCalendars")
//    public List<Cards> findAll(){
//        return cardsService.findAll();
//    }

    //내 캘린더 조회
    @GetMapping("/cards/calendars")
    @LogExecutionTime
    public List<Cards> getMyCalendars() {
        return cardsService.getMyCalendars();
    }

    //카드 작성
    @PostMapping("/cards")
    public String createCard(@RequestBody CardsRequestDto requestDto) {
        return cardsService.save(requestDto);
    }

    //수정
    @PutMapping("/cards/{selectedAt}")
    public String update(@PathVariable("selectedAt") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate selectedAt,
                         @RequestBody CardsRequestDto requestDto) {
        return cardsService.update(selectedAt,requestDto);
    }

    //상세 조회
    @GetMapping("/cards/{selectedAt}")
    public Cards findBySelectedAt(@PathVariable("selectedAt") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate selectedAt) {
        return cardsService.findBySelectedAt(selectedAt);
    }

    //삭제
    @DeleteMapping("/cards/{selectedAt}")
    public String delete(@PathVariable("selectedAt") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate selectedAt) {
        return cardsService.delete(selectedAt);
    }

}
