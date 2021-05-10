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
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    public void configure(WebSecurity web)  {
        web.ignoring()
                .antMatchers("/favicon.ico", "/error");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors();
        http.csrf().disable();
        http.headers().frameOptions().disable();
        http.authorizeRequests()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/api/login").permitAll()
                .antMatchers(HttpMethod.POST, "/signup").permitAll()
                //서버에서 하는 테스트용
                .antMatchers("/oauth/kakao").permitAll()
                .antMatchers("/oauth/callback/kakao").permitAll()
                .antMatchers(HttpMethod.POST, "/oauth/callback/kakao").permitAll()
                .antMatchers("/username").permitAll()
                .antMatchers("/username/{username}").permitAll()
                .antMatchers("/email").permitAll()
                .antMatchers("/email/certification/send").permitAll()
                .antMatchers("/email/certification/confirm").permitAll()
                .antMatchers("/chart/lineChart/{today}").permitAll();

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

                .anyRequest().authenticated()

                //JstSecurityConfig(커스텀 필터 등록해놓은) 등록. 커스텀 필터를 쓰겠다.
                .and()
                .apply(new JwtSecurityConfig(jwtTokenProvider));
    }
}
