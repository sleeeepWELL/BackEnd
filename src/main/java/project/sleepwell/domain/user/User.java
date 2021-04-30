package project.sleepwell.domain.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;    //nickname 처럼 활용

    private String email;

    private String password;


    @Enumerated(EnumType.STRING)
    private Authority authority;

    private Long kakaoId;

    @Builder
    public User(String username, String email, String password, Authority authority) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.authority = authority;
        this.kakaoId = null;
    }

    public User(String username, String password, String email, Authority authority, Long kakaoId) {  //email 이 없음
        this.username = username;
        this.password = password;
        this.email = email;
        this.authority = Authority.ROLE_USER;       //authority 해도 됨.
        this.kakaoId = kakaoId;
    }


}