package project.sleepwell.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {

    private String email;
    private String password;

    public UsernamePasswordAuthenticationToken toAuthentication() {
        //Object principal, Object credentials
        return new UsernamePasswordAuthenticationToken(email, password);
    }
}
