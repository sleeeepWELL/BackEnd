package project.sleepwell.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LineChartResponseDto {


    private LocalDate date;
    private Double mySleepTime;
    private Double adequateSleepTime;


}
