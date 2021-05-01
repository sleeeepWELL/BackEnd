package project.sleepwell.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//요청 받을 때 한번만 실행되도록. GenericFilterBean 필터 상위버젼
//

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;    //== final ==//

    //HttpServletRequest 라 형변환 해줄 필요 없음
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //헤더에서 토큰 꺼내기
        String token = resolveToken(request);

        //토큰이 유효하다면
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            //인증 정보 가져와서
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            //SecurityContext 에 넣기
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            //토큰 값만 리턴
            return bearerToken.substring(7);
        }
        return null;
    }
}
