package project.sleepwell.web.dto;


import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;
import project.sleepwell.domain.user.Authority;
import project.sleepwell.domain.user.User;
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


    public User toUser(PasswordEncoder passwordEncoder, String validatedEmail) {
        return User.builder()
                .email(validatedEmail)
                .username(username)
                .password(passwordEncoder.encode(password))
                .authority(Authority.ROLE_USER)
                .build();
    }

}
