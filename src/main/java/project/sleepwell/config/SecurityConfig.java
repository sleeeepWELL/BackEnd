package project.sleepwell.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import project.sleepwell.jwt.JwtAccessDeniedHandler;
import project.sleepwell.jwt.JwtAuthenticationEntryPoint;
import project.sleepwell.jwt.JwtSecurityConfig;
import project.sleepwell.jwt.JwtTokenProvider;
import project.sleepwell.security.oauth2.CustomOAuth2UserService;
import project.sleepwell.domain.Role;


@RequiredArgsConstructor
@EnableWebSecurity      //spring security 활성화
//securityMethod 활성화
//@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    private final CustomUserDetailsService customUserDetailsService;

    private final CustomOAuth2UserService customOAuth2UserService;

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    //h2 console 로 테스트 할 것
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("h2-console/**", "/favicon.ico");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()

                //핸들러 등록해놓기
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                //h2 console disable or sameOrigin 설정 (x frame option 동일 출처일 경우만 허용하도록)
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                //jwt 토큰 인증이므로 세션은 사용하지 않겠다. stateless 로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers("/", "css/**", "images/**", "js/**").permitAll()
                .antMatchers("/login", "/signup").permitAll()   //로그인, 회원가입 api 는 열어둠
                .anyRequest().authenticated()

                //JstSecurityConfig(커스텀 필터 등록해놓은) 등록. 커스텀 필터를 쓰겠다.
                .and()
                .apply(new JwtSecurityConfig(jwtTokenProvider))

                //
                .and()
                .oauth2Login()
                .userInfoEndpoint() //로그인 성공 이후 사용자 정보를 가져올 때의 설정들 담당
                .userService(customOAuth2UserService);
    }
}
