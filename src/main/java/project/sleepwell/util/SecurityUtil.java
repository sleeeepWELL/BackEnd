package project.sleepwell.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    private SecurityUtil(){}

    //username 꺼내기
    public static String getCurrentUsername() {
        //인증 정보 꺼내기
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();   //== final 로 지정==/

        if (authentication == null) {
            throw new RuntimeException("Security Context 에 인증 정보가 없습니다.");
        }

        //지금은 우선 그냥 쓰지만, 추후에 유저 권한에 따른 유저네임 뽑기 로직 추가해야 함.

        return authentication.getName();

    }
}
