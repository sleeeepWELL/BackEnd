package project.sleepwell.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TokenRequestDto {

    private String accessToken;
    private String refreshToken;
}
