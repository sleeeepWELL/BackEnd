package project.sleepwell.domain.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import project.sleepwell.domain.user.UserRepository;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

import static project.sleepwell.domain.email.RandomNumberGeneration.makeRandomNumber;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailCertificationService {

    private final EmailCertificationRepository emailCertificationRepository;
    private final JavaMailSender emailSender;
    private final UserRepository userRepository;


    public void sendEmail(String email) throws UnsupportedEncodingException, MessagingException {

        String validatedEmail = email.replaceAll(" ", "");

        if (userRepository.existsByEmail(validatedEmail)) {
            throw new RuntimeException("이미 사용 중인 이메일 입니다.");
        }

        String randomNumber = makeRandomNumber();
        MimeMessage message = createMessage(validatedEmail, randomNumber);
        try{
            emailSender.send(message);
            log.info("send authorize code to email.");

        }catch(MailException es){
            es.printStackTrace();
            throw new IllegalArgumentException();
        }

        emailCertificationRepository.createEmailCertification(validatedEmail, randomNumber);
    }

    //파라미터로 받은 이메일로 인증번호 발송 (비밀번호 재설정용)
    public void sendEmailToChangePw(String email) throws UnsupportedEncodingException, MessagingException {

        userRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("The email does not exist !")
        );

        String randomNumber = makeRandomNumber();

        MimeMessage message = createMessageToChangePw(email, randomNumber);
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

        String validatedEmail = requestDto.getEmail().replaceAll(" ", "");

        if (isVerify(requestDto)) {
            throw new CertificationNumberMismatchException("인증번호가 일치하지 않습니다.");
        }

        emailCertificationRepository.removeEmailCertification(validatedEmail);
    }

    private boolean isVerify(EmailCertificationRequestDto requestDto) {

        String validatedEmail = requestDto.getEmail().replaceAll(" ", "");

        boolean isExistKey = emailCertificationRepository.hasKey(validatedEmail);
        String findCtfKey = emailCertificationRepository.getEmailCertificationNum(validatedEmail);

        String validatedNumber = requestDto.getCertificationNumber().replaceAll(" ", "");

        return !(isExistKey && findCtfKey.equals(validatedNumber));
    }


    //이메일 보내기 (회원가입 시 인증용 이메일)
    private MimeMessage createMessage(String email, String randomNumber) throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = emailSender.createMimeMessage();
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
        msg += "<a href=\"https://teamsleepwell.com\" style=\"padding-right: 30px; padding-left: 30px; text-decoration: none; color: #434245;\" rel=\"noreferrer noopener\" target=\"_blank\">2021 SleepWell</a>";

        message.setText(msg, "utf-8", "html");
        message.setFrom(new InternetAddress("yihyeonehyeon@gmail.com","SleepWell"));

        return message;
    }

    //이메일 보내기 (비밀번호 찾기 시 인증용)
    private MimeMessage createMessageToChangePw(String email, String randomNumber) throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = emailSender.createMimeMessage();
        //to.you
        message.addRecipients(MimeMessage.RecipientType.TO, email);
        //mail title (상단에 노출)
        message.setSubject("[SleepWell] 비밀번호 재설정 이메일 입니다");
        //content
        String msg="";
        msg += "<img width=\"120\" height=\"36\" style=\"margin-top: 0; margin-right: 0; margin-bottom: 32px; margin-left: 0px; padding-right: 30px; padding-left: 30px;\" src=\"https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FRYeu1%2Fbtq4s2GjDUS%2FhHSOoZ1s9GlSmKMHKGd5m0%2Fimg.png\" alt=\"\" loading=\"lazy\">";
        msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">SleepWell 비밀번호 재설정</h1>";
        msg += "<p style=\"font-size: 14px; padding-right: 30px; padding-left: 30px;\">" +
                "비밀번호를 다시 설정하려면 아래 해당 인증번호를 인증번호 확인란에 입력하여 주십시오.<br>"+
                "<br>" +
                "만약 새로운 비밀번호 설정을 요청하신 적이 없다면 이 메일을 무시해주세요.<br> " +
                "기존 비밀번호가 유지 됩니다." +
                "</p>";
        msg += "<div style=\"padding-right: 30px; padding-left: 30px; margin: 32px 0 40px;\"><table style=\"border-collapse: collapse; border: 0; background-color: #F4F4F4; height: 70px; table-layout: fixed; word-wrap: break-word; border-radius: 6px;\"><tbody><tr><td style=\"text-align: center; vertical-align: middle; font-size: 30px;\">";
        msg += randomNumber;
        msg += "</td></tr></tbody></table></div>";
        msg += "<p style=\"font-size: 13px; padding-right: 30px; padding-left: 30px;\">" +
                "이 인증번호는 3분 동안 유효합니다."+
                "</p>";
        msg += "<a href=\"https://teamsleepwell.com\" style=\"padding-right: 30px; padding-left: 30px; text-decoration: none; color: #434245;\" rel=\"noreferrer noopener\" target=\"_blank\">2021 SleepWell</a>";

        message.setText(msg, "utf-8", "html");
        message.setFrom(new InternetAddress("yihyeonehyeon@gmail.com","SleepWell"));

        return message;
    }

}
