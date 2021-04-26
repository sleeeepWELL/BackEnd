package project.sleepwell.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import project.sleepwell.service.cards.CardsService;
import project.sleepwell.web.dto.CardsRequestDto;
import project.sleepwell.web.dto.CardsResponseDto;

@RequiredArgsConstructor
@RestController
public class CardsRestController {

    private final CardsService cardsService;

    @PostMapping("/cards")
    public String save(@RequestBody CardsRequestDto requestDto) {
        return cardsService.save(requestDto);
    }

    @PutMapping("/cards/{id}")
    public String update(@PathVariable Long id, @RequestBody CardsRequestDto requestDto) {
        return cardsService.update(id,requestDto);
    }

    @GetMapping("/cards/{id}")
    public CardsResponseDto findById(@PathVariable Long id) {
        return cardsService.findById(id);
    }

}
