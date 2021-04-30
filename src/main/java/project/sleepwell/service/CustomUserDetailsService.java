package project.sleepwell.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import project.sleepwell.domain.user.User;
import project.sleepwell.domain.user.UserRepository;

import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //== 이게 도대체 뭔데 findByUsername 은 401 에러가 뜨는 거지 ==// 한 2시간 날린 거 같은데
        Optional<User> user = userRepository.findByEmail(username);
        return user.map(this::principalDetails)
                    .orElseThrow(() -> new UsernameNotFoundException("데이터베이스에서 찾을 수 없습니다."));

    }

    //db 에 User 가 존재하면, 객체 생성
    //UserDetails 구현한 클래스 만들어도 됨.
    private UserDetails principalDetails(User user) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getAuthority().toString());

        return new org.springframework.security.core.userdetails.User(
                String.valueOf(user.getId()),
                user.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }
}
