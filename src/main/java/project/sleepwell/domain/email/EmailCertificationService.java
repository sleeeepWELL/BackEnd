package project.sleepwell.domain.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import project.sleepwell.domain.user.CertificationNumberMismatchException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import static project.sleepwell.domain.email.RandomNumberGeneration.makeRandomNumber;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailCertificationService {

    private final EmailCertificationRepository emailCertificationRepository;
    private final JavaMailSender emailSender;
//    private static final String keyNum = createKey();


    //email 로 인증번호를 발송하고, 발송 정보(email, certificationNumber)를 Redis 에 저장
    public void sendEmail(String email) throws UnsupportedEncodingException, MessagingException {

        String randomNumber = makeRandomNumber();

        MimeMessage message = createMessage(email, randomNumber);
        try{
            emailSender.send(message);
            log.info("send authorize code to email.");
        }catch(MailException es){
            es.printStackTrace();
            throw new IllegalArgumentException();
        }

        emailCertificationRepository.createEmailCertification(email, randomNumber);
    }

    //유저가 입력한 인증번호가 Redis 에 저장된 인증번호와 일치하는지 확인
    public void verifyEmail(EmailCertificationRequestDto requestDto) {
        //true == 인증번호 불일치
        if (isVerify(requestDto)) {
            throw new CertificationNumberMismatchException("인증번호가 일치하지 않습니다.");
        }
        //인증번호가 일치하면 redis 에 저장된 인증번호를 삭제
        emailCertificationRepository.removeEmailCertification(requestDto.getEmail());
    }

    private boolean isVerify(EmailCertificationRequestDto requestDto) {
        boolean isExistKey = emailCertificationRepository.hasKey(requestDto.getEmail());
        String findCtfKey = emailCertificationRepository.getEmailCertificationNum(requestDto.getEmail());

        //redis 에 저장된 인증번호 != 유저가 입력한 인증번호. return true
        //하나라도 false 가 되면 결과는 !false == true 가 돼서 if 문에 들어감.
        return !(isExistKey && findCtfKey.equals(requestDto.getCertificationNumber()));
    }


    private MimeMessage createMessage(String email, String randomNumber) throws MessagingException, UnsupportedEncodingException {
        log.info("받는 사람 = {}", email);
        log.info("인증 번호 = {}", randomNumber);

        MimeMessage message = emailSender.createMimeMessage();
//        String verificationCode = settingCode(keyNum);
        //to.you
        message.addRecipients(MimeMessage.RecipientType.TO, email);
        //mail title (상단에 노출)
        message.setSubject("[SleepWell] 환영합니다. SleepWell 회원 가입 인증 이메일 입니다.");
        //content
        String msg="";
        msg += "<img width=\"120\" height=\"36\" style=\"margin-top: 0; margin-right: 0; margin-bottom: 32px; margin-left: 0px; padding-right: 30px; padding-left: 30px;\" src=\"https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FRYeu1%2Fbtq4s2GjDUS%2FhHSOoZ1s9GlSmKMHKGd5m0%2Fimg.png\" alt=\"\" loading=\"lazy\">";
        msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">SleepWell 홈페이지를 방문해주셔서 감사합니다.</h1>";
        msg += "<p style=\"font-size: 14px; padding-right: 30px; padding-left: 30px;\">" +
                "<b>안녕하세요!</b> 회원 가입을 계속 하시려면 해당 인증번호를 인증번호 확인란에 기입해주세요.<br>"+
                "만약에 실수로 요청하셨거나, 본인이 요청하지 않았다면, 이 메일을 무시하세요. " +
                "</p>";
        msg += "<div style=\"padding-right: 30px; padding-left: 30px; margin: 32px 0 40px;\"><table style=\"border-collapse: collapse; border: 0; background-color: #F4F4F4; height: 70px; table-layout: fixed; word-wrap: break-word; border-radius: 6px;\"><tbody><tr><td style=\"text-align: center; vertical-align: middle; font-size: 30px;\">";
        msg += randomNumber;
        msg += "</td></tr></tbody></table></div>";
        msg += "<p style=\"font-size: 13px; padding-right: 30px; padding-left: 30px;\">" +
                "이 인증번호는 3분 동안 유효합니다."+
                "</p>";
        msg += "<a href=\"https://sleepwell.com\" style=\"padding-right: 30px; padding-left: 30px; text-decoration: none; color: #434245;\" rel=\"noreferrer noopener\" target=\"_blank\">2021 SleepWell</a>";

        message.setText(msg, "utf-8", "html");
        message.setFrom(new InternetAddress("yihyeonehyeon@gmail.com","SleepWell"));

        return message;
    }

}