package project.sleepwell.web;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import project.sleepwell.service.ChartService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ChartController {

    private final ChartService chartService;

    // 컨디션 2보다 높은 Total sleep 찾기 -> 마지막 추천 문구
    @GetMapping("/yourSleepTime")
    public List<Integer> yourSleepTimeByConditions(@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        return chartService.yoursleeptimebyconditions(principal);
    }
}
