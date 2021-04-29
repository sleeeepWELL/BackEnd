package project.sleepwell.dto;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.validation.constraints.NotNull;

public class LoginDto {

    @NotNull
    private String email;

    @NotNull
    private String password;

    public UsernamePasswordAuthenticationToken toAuthentication() {
        //Object principal, Object credentials
        return new UsernamePasswordAuthenticationToken(email, password);
    }
}
