package project.sleepwell.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import project.sleepwell.jwt.JwtAccessDeniedHandler;
import project.sleepwell.jwt.JwtAuthenticationEntryPoint;
import project.sleepwell.jwt.JwtSecurityConfig;
import project.sleepwell.jwt.JwtTokenProvider;


@RequiredArgsConstructor
@EnableWebSecurity      //spring security 활성화
//securityMethod 활성화
//@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    private final CustomUserDetailsService customUserDetailsService;

//    private final CustomOAuth2UserService customOAuth2UserService;

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //kakao login
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    //h2 console 로 테스트 할 것
    @Override
    public void configure(WebSecurity web)  {
        web.ignoring()
                .antMatchers("/favicon.ico", "/error");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors();
        http.csrf().disable();
        http.headers().frameOptions().disable();    //sameOrigin 은 왜 안먹히냐고 아오 진짜. 1시간 날림
        http.authorizeRequests()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/api/login").permitAll()
//                .antMatchers("/api/login/code").permitAll()     //응답 코드 테스트용
//                .antMatchers("/kakaoLogin").permitAll()
                //서버에서 하는 테스트용
                .antMatchers("/oauth/kakao").permitAll()
                .antMatchers("/oauth/callback/kakao").permitAll()
                .antMatchers(HttpMethod.POST, "/oauth/callback/kakao").permitAll()
                .antMatchers("/authorize/code").permitAll()
                .antMatchers("/username").permitAll();




        http
                //핸들러 등록해놓기
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                //jwt 토큰 인증이므로 세션은 사용하지 않겠다. stateless 로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/signup").permitAll()
                .antMatchers( "/test").permitAll()
                .antMatchers( "/only/user").hasRole("USER")
                .antMatchers( "/test/authentication/test").hasRole("USER")

                .anyRequest().authenticated()

                //JstSecurityConfig(커스텀 필터 등록해놓은) 등록. 커스텀 필터를 쓰겠다.
                .and()
                .apply(new JwtSecurityConfig(jwtTokenProvider));

                //
//                .and()
//                .oauth2Login()
//                .userInfoEndpoint() //로그인 성공 이후 사용자 정보를 가져올 때의 설정들 담당
//                .userService(customOAuth2UserService);
    }
}
