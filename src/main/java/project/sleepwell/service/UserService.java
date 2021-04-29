package project.sleepwell.service;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.sleepwell.domain.User;
import project.sleepwell.dto.LoginDto;
import project.sleepwell.dto.SignupRequestDto;
import project.sleepwell.dto.UserResponseDto;
import project.sleepwell.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    /**
     * create user
     * email, username, password, passwordCheck
     */
    public Long createUser(SignupRequestDto signupRequestDto) {
        if (userRepository.existsByEmail(signupRequestDto.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일 입니다.");
        }

        if (!signupRequestDto.getPassword().equals(signupRequestDto.getPasswordCheck())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        User user = signupRequestDto.toUser(passwordEncoder);
        return userRepository.save(user).getId();

    }

    public Long login(LoginDto loginDto) {

    }

    //== 체크 메서드 ==//
//    public void isAvailable(String email) {
//        if (userRepository.findByEmail(email).isPresent()) {
//            throw new IllegalArgumentException("이미 사용 중인 이메일 입니다.");
//        }
//    }










    //처음부터 인자로 email 을 받아서 유저 정보를 찾고, email 다시 반환
    @Transactional(readOnly = true)
    public UserResponseDto getUserInfo(String email) {
        return userRepository.findByEmail(email)
                .map(UserResponseDto::of)       //stream
                .orElseThrow(
                        () -> new RuntimeException("유저 정보가 존재하지 않습니다.")
                );
    }

    //SecurityUtil 에서 로그인 한 유저 정보를 찾아서 email 만 반환
    @Transactional(readOnly = true)
    public UserResponseDto getMyInfo() {    //username 을 반환하는 게 의미가 없어서 id 만 반환하게 함
        return userRepository.findById(SecurityUtil.getCurrentUserId())
                .map(UserResponseDto::of)
                .orElseThrow(
                        () -> new RuntimeException("로그인 한 유저의 정보가 존재하지 않습니다.")
                );
    }



}
