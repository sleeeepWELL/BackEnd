package project.sleepwell.dto;

import javax.validation.constraints.NotNull;

public class LoginDto {

    @NotNull
    private String email;

    @NotNull
    private String password;
}
