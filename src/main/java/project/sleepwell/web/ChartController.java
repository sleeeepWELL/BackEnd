package project.sleepwell.web;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import project.sleepwell.service.ChartService;
import project.sleepwell.util.LogExecutionTime;
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
    @LogExecutionTime
    public List<Integer> yourSleepTimeByConditions() {
        return chartService.yourSleepTimeByConditions();
    }

    // 오늘을 기준으로 주간,월간 태그의 빈도수 구하기 그래프
    @GetMapping("/chart/barChart/{today}")
    @LogExecutionTime
    public List<List<Integer>> tagBarChart (@PathVariable("today") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate today) {
        return chartService.tagBarChart(today);
    }

    // 현재까지의 컨디션 -> 잔디심기
    @GetMapping("/chart/grassChart")
    @LogExecutionTime
    public List<Map<String,Object>> grassChart () {
        return chartService.grassChart();
    }

    //주간 - 적정 수면 시간과 나의 수면 시간
    @GetMapping("/chart/lineChart/{today}")
    @LogExecutionTime
    public List<LineChartResponseDto> compareToSleeptime(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate today) {
        return chartService.compareToSleeptime(today);
    }

    //요약 테이블
    @GetMapping("/chart/table/{today}")
    @LogExecutionTime
    public List<List<Integer>> summaryTable (@PathVariable("today") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate today) {
        return chartService.summaryTable(today);
    }
}
