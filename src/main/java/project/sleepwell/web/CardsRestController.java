package project.sleepwell.web;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.sleepwell.service.CardsService;
import project.sleepwell.web.dto.CardsRequestDto;
import project.sleepwell.web.dto.CardsResponseDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class CardsRestController {

    @ExceptionHandler(IllegalArgumentException.class)
    public String exceptionHandler(Exception e){
        return e.getMessage();
    }

    private final CardsService cardsService;

    //전체 조회(관리자용..? 모든 카드 조회 - 혹시 몰라서 남겨둠.)
    @GetMapping("/allCalendars")
    public List<CardsResponseDto> findAll(){
        return cardsService.findAll();
    }

    //내 캘린더 조회
    @GetMapping("/calendars")
    public Map<String, Object> getMyCalendars(@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        return cardsService.getMyCalendars(principal);   //calendarInfo 반환하게
    }

    //카드 작성
    @PostMapping("/cards")
    public Long createCard(@RequestBody CardsRequestDto requestDto, @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        return cardsService.createCard(requestDto, principal);
    }

    //수정
    @PutMapping("/cards/{selectedAt}")
    public String update(@PathVariable("selectedAt") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate selectedAt, @RequestBody CardsRequestDto requestDto, @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        return cardsService.update(selectedAt,requestDto,principal);
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
