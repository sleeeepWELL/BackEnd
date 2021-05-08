package project.sleepwell.domain.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Random;

@RequiredArgsConstructor
@Slf4j
@Service
public class EmailService {

    private final JavaMailSender emailSender;
    private static final String keyNum = createKey();



    private MimeMessage createMessage(String to) throws MessagingException, UnsupportedEncodingException {
        log.info("받는 사람 = {}", to);
        log.info("인증 번호 = {}", keyNum);

        MimeMessage message = emailSender.createMimeMessage();
        String verificationCode = settingCode(keyNum);
        //to.you
        message.addRecipients(MimeMessage.RecipientType.TO, to);
        //mail title (상단에 노출)
        message.setSubject("환영합니다. SleepWell 회원 가입 인증 이메일 입니다.");
        //content
        String msg="";
        msg += "<img width=\"120\" height=\"36\" style=\"margin-top: 0; margin-right: 0; margin-bottom: 32px; margin-left: 0px; padding-right: 30px; padding-left: 30px;\" src=\"sleepwell.png\" alt=\"\" loading=\"lazy\">";
        msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">SleepWell 홈페이지를 방문해주셔서 감사합니다.</h1>";
        msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">해당 인증번호를 인증번호 확인란에 기입해주세요.</p>";
        msg += "<div style=\"padding-right: 30px; padding-left: 30px; margin: 32px 0 40px;\"><table style=\"border-collapse: collapse; border: 0; background-color: #F4F4F4; height: 70px; table-layout: fixed; word-wrap: break-word; border-radius: 6px;\"><tbody><tr><td style=\"text-align: center; vertical-align: middle; font-size: 30px;\">";
        msg += verificationCode;
        msg += "</td></tr></tbody></table></div>";
        msg += "<a href=\"https://sleepwell.com\" style=\"text-decoration: none; color: #434245;\" rel=\"noreferrer noopener\" target=\"_blank\">SleepWell Website</a>";

        message.setText(msg, "utf-8", "html");
        message.setFrom(new InternetAddress("yihyeonehyeon@gmail.com","SleepWell")); //보내는 사람

        return message;
    }

    //인증 코드 만드는 메서드 (static 이 아니라 DB 에 저장하기)
    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        //8자리 인증코드
        for (int i = 0; i < 8; i++) {
            key.append(rnd.nextInt(10));
        }
        return key.toString();
    }

    //코드 조합
    public String settingCode(String keyNum) {
        return keyNum.substring(0, 3)
                + "-" + keyNum.substring(3, 5)
                + "-" + keyNum.substring(5, 8);
    }

    public void sendSimpleMessage(String to) throws UnsupportedEncodingException, MessagingException {
        MimeMessage message = createMessage(to);
        try{
            emailSender.send(message);
            log.info("send authorize code to email.");
        }catch(MailException es){
            es.printStackTrace();
            throw new IllegalArgumentException();
        }

    }
}
