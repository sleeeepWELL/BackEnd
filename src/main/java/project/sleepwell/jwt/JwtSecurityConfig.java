package project.sleepwell.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 내가 만든 JwtTokenProvider 와 JwtFilter 를 SecurityConfig 에 등록하기 위해 JwtSecurityConfig 를 만듦.
 * UsernamePasswordAuthenticationFilter 가 작동 되기 전에
 * 내가 custom 한 JwtAuthenticationFilter 가 먼저 작동 되도록 설정.
 */
@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final JwtTokenProvider jwtTokenProvider;    //==final=//

    @Override
    public void configure(HttpSecurity http) {
        JwtFilter customFilter = new JwtFilter(jwtTokenProvider);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
