package project.sleepwell.domain;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;    //nickname 처럼 활용

    @NotNull
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

    public User(String username, String password, Authority authority, Long kakaoId) {
        this.username = username;
        this.password = password;
        this.authority = Authority.ROLE_USER;
        this.kakaoId = kakaoId;
    }


}