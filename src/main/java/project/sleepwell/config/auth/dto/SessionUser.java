package project.sleepwell.config.auth.dto;

import lombok.Getter;
import project.sleepwell.domain.User;

/**
 * 인증된 사용자 정보만 필요
 */
@Getter
public class SessionUser {
    private String name;
    private String email;
    private String picture;

    public SessionUser(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.picture = user.getPicture();
    }
}
