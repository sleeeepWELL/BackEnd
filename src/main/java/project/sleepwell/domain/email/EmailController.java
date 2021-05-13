package project.sleepwell.domain.email;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
public class EmailController {

    @ExceptionHandler(IllegalArgumentException.class)
    public String exceptionHandler(Exception e){
        return e.getMessage();
    }

    private final EmailCertificationService emailCertificationService;


    //이메일로 인증 번호 보내는 api(회원가입 인증용)
    @PostMapping("/email/certification/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailCertificationRequestDto requestDto) throws UnsupportedEncodingException, MessagingException {
        emailCertificationService.sendEmail(requestDto.getEmail());
        return ResponseEntity.ok("ok");
    }

    //인증번호 확인하는 api
    @PostMapping("/email/certification/confirm")
    public ResponseEntity<String> emailVerification(@RequestBody EmailCertificationRequestDto requestDto) {
        emailCertificationService.verifyEmail(requestDto);
        return ResponseEntity.ok("ok");
    }

    //이메일로 인증 번호 보내는 api(비밀번호 찾기용)
    @PostMapping("/email/certification/send/reset")
    public ResponseEntity<String> sendEmailToChangePw(@RequestBody EmailCertificationRequestDto requestDto) throws UnsupportedEncodingException, MessagingException {
        emailCertificationService.sendEmailToChangePw(requestDto.getEmail());
        return ResponseEntity.ok("ok");
    }
}
