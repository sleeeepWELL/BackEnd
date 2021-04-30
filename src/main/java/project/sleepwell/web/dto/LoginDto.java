package project.sleepwell.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

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
