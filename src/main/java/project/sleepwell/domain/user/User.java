package project.sleepwell.domain.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.sleepwell.domain.Timestamped;
import project.sleepwell.domain.cards.Cards;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class User extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Cards> cards = new ArrayList<>();

    private String username;

    private String email;

    private String password;


    @Enumerated(EnumType.STRING)
    private Authority authority;

    private Long kakaoId;

    public void setKakaoId(Long kakaoId) {
        this.kakaoId = kakaoId;
    }

    @Builder
    public User(String username, String email, String password, Authority authority) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.authority = authority;
        this.kakaoId = null;
    }

    public User(String username, String password, String email, Authority authority, Long kakaoId) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.authority = Authority.ROLE_USER;
        this.kakaoId = kakaoId;
    }


    //change password
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    //change username
    public void updateUsername(String username) {
        this.username = username;
    }


}
