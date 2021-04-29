package project.sleepwell.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import project.sleepwell.domain.Role;
import project.sleepwell.domain.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 내가 생성한 User 클래스를 spring security 와 연결.
 * 연결하기 위해 UserDetails 와 UserDetailsService 를 Implements 해야 함.
 */
public class UserPrincipal implements UserDetails {

    private User user;

    public UserPrincipal(User user) {   //spring security 에 user 정보 전달
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        //role 처리를 어떻게 해줘야 할지?
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(this.user.getRole(Role.USER));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
