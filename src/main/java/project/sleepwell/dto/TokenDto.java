package project.sleepwell.dto;

import lombok.Builder;
import lombok.Getter;

@Builder    //token provider 에서 토큰 만들어서 빌드할 때
@Getter
public class TokenDto {

    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiresIn;
}
