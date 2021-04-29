package project.sleepwell.dto;


import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import project.sleepwell.domain.Authority;
import project.sleepwell.domain.User;

import javax.validation.constraints.NotNull;

@Getter
public class SignupRequestDto {

    @NotNull
    private String email;

    @NotNull
    private String username;

    @NotNull
    private String password;

    @NotNull
    private String passwordCheck;

    //service 에서 하는 것과 dto 에서 하는 것 차이
    public User toUser(PasswordEncoder passwordEncoder) {
        return User.builder()
                .email(email)
                .username(username)
                .password(passwordEncoder.encode(password))
                .authority(Authority.ROLE_USER)
                .build();
    }

    public UsernamePasswordAuthenticationToken toAuthentication() {
        //Object principal, Object credentials
        return new UsernamePasswordAuthenticationToken(email, password);
    }

}
