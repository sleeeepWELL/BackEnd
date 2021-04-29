package project.sleepwell.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

//custom 해서 쓰기. (username)
public class SecurityUtil {

    private SecurityUtil(){}

    //username 을 nickname 처럼 쓸 거라서 username 을 반환할 필요가 없음.
    //user id 를 반환하도록 변환
    public static Long getCurrentUserId() {
        //인증 정보 꺼내기
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();   //== final 로 지정==/

        if (authentication == null) {
            throw new RuntimeException("Security Context 에 인증 정보가 없습니다.");
        }

        //이게 어떻게 되는 거지?
        return Long.parseLong(authentication.getName());


    }
}
