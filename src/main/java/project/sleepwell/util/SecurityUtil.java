package project.sleepwell.util;

import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static java.lang.Long.parseLong;

//customizing
@NoArgsConstructor
public class SecurityUtil {

//    private SecurityUtil(){}

    //username 꺼내기
    public static String getCurrentUsername() {
        //인증 정보 꺼내기
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();   //== final 로 지정==/

        if (authentication == null) {
            throw new RuntimeException("Security Context 에 인증 정보가 없습니다.");
        }
        return authentication.getName();
    }

    //user id 꺼내기
    public static Long getCurrentUserId() {
        //인증 정보 꺼내기
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new RuntimeException("Security Context 에 인증 정보가 없습니다.");
        }
        return parseLong(authentication.getName());
    }

}
