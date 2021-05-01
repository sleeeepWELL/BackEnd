package project.sleepwell.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.sleepwell.domain.user.User;
import project.sleepwell.domain.user.UserRepository;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * username == email (LoginDto 에서 로그인 한 회원의 username 을 email 로 바꿔서 생성하게 만듦.)
     * -> new UsernamePasswordAuthenticationToken(email, password);
     * @param email
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //사실상 loadUserByEmail
        log.info("loadUserByUserName.String username = {}", email);
        Optional<User> user = userRepository.findByEmail(email);

        return user.map(this::principalDetails)
                    .orElseThrow(() -> new UsernameNotFoundException("데이터베이스에서 찾을 수 없습니다."));
        //id, password, authority 를 갖고 있는 user
    }

    /**
     * db 에 User 가 존재하면, 객체 생성
     * UserDetails 구현한 클래스 만들어도 됨.
     * 필요에 따라 커스터마이징
     * spring User setting = username = '(String)id', password = password, authority = authority
     * spring User setting = username = 'username', password = password, authority = authority
     * @param user
     * @return
     */
    private UserDetails principalDetails(User user) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getAuthority().toString());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
//                String.valueOf(user.getId()),
                user.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }
}
