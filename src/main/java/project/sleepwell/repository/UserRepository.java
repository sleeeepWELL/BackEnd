package project.sleepwell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.sleepwell.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    //로그인 할 때 email 사용
    Optional<User> findByEmail(String email);

    //중복 가입 방지
    boolean existsByEmail(String email);

    //custom service
    Optional<User> findByUsername(String username);
}
