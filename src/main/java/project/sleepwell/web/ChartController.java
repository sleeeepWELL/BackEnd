package project.sleepwell.web;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import project.sleepwell.service.ChartService;
import project.sleepwell.web.dto.LineChartResponseDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class ChartController {

    private final ChartService chartService;

    // 컨디션 2보다 높은 Total sleep 찾기 -> 마지막 추천 문구
    @GetMapping("/chart/yourSleepTime")
    public List<Integer> yourSleepTimeByConditions(@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        return chartService.yoursleeptimebyconditions(principal);
    }

    // 오늘을 기준으로 주간,월간 태그의 빈도수 구하기 그래프
    @GetMapping("/chart/barChart/{today}")
    public List<List<Integer>> tagBarChart (@PathVariable("today") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate today, @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        return chartService.tagbarchart(today,principal);
    }


    @GetMapping("/chart/grassChart")
    public List<Map<String,Object>> grassChart (@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        return chartService.grassChart(principal);
    }

    @GetMapping("/chart/table/{today}")
    public List<List<Integer>> weeklyTable (@PathVariable("today") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate today, @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        return chartService.weeklyTable(today,principal);
    }


    //주간 - 적정 수면 시간과 나의 수면 시간
    @GetMapping("/chart/lineChart/{today}")
    public List<LineChartResponseDto> compareToSleeptime(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate today,
                                                         @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        return chartService.compareToSleeptime(today, principal);
    }
}
