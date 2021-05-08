package project.sleepwell.domain.email;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@RequiredArgsConstructor
@Repository
public class EmailCertificationRepository {

    //key
    private final String PREFIX = "email: ";
    //3분 시간제한
    private final int LIMIT_TIME = 3 * 60;

    private final StringRedisTemplate stringRedisTemplate;

    //발송 정보(email, certificationNumber)를 Redis 에 저장
    public void createEmailCertification(String email, String certificationNumber) {
        //key: email, value: certificationNumber, 3분 시간제한 설정
        stringRedisTemplate.opsForValue()
                .set(PREFIX + email, certificationNumber, Duration.ofSeconds(LIMIT_TIME));
    }

    //특정 이메일에 해당하는 인증번호 가져오기
    public String getEmailCertificationNum(String email) {
        return stringRedisTemplate.opsForValue().get(PREFIX + email);
    }

    //이메일 검증이 완료되면 인증번호 삭제하기
    public void removeEmailCertification(String email) {
        stringRedisTemplate.delete(PREFIX + email);
    }

    //redis 에 해당 이메일로 저장된 인증번호가 존재하는지 여부 확인하기
    public boolean hasKey(String email) {
        return stringRedisTemplate.hasKey(PREFIX + email);
    }


}
