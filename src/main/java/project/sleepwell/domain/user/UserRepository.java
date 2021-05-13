package project.sleepwell.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    //로그인 시
    Optional<User> findByEmail(String email);

    //중복 가입 방지
    boolean existsByEmail(String email);

    //username 중복 체크
    boolean existsByUsername(String username);

    //custom service
    Optional<User> findByUsername(String username);

    //kakao login
    Optional<User> findByKakaoId(Long kakaoId);
}
