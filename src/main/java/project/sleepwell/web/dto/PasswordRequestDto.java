package project.sleepwell.web.dto;

import lombok.Getter;

@Getter
public class PasswordRequestDto {

    private String email;

    private String password;

    private String passwordCheck;

}
