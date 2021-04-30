package project.sleepwell.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.sleepwell.domain.User;

//response 는 안쓴다.
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {  //email 만 반환

    private String email;

    //static
    public static UserResponseDto of(User user) {
        return new UserResponseDto(user.getEmail());
    }
}
