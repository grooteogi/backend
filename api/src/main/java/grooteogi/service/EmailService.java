package grooteogi.service;

import grooteogi.dto.EmailCodeRequest;
import grooteogi.dto.EmailRequest;
import grooteogi.repository.UserRepository;
import grooteogi.utils.RedisClient;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    private final RedisClient redisClient;

    private final String prefix = "email_verify";

    public boolean isExist(EmailRequest email) {
        return userRepository.existsByEmail(email.getEmail());
    }

    public void createEmailVerification(EmailRequest email) {
        String code = createCode();
        sendMail(email.getEmail(), code);
    }

    public boolean confirmEmailVerification(EmailCodeRequest emailCodeRequest)  {
        String key = prefix + emailCodeRequest.getEmail();
        String value = redisClient.getValue(key);
        if(value != null) {
            return value.equals(emailCodeRequest.getCode());
        } else return false;
    }

    private static String createCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for(int i =0; i< 3; i++){
            int idx = random.nextInt(25)+65;
            code.append((char) idx);
        }
        int numIdx = random.nextInt(9999)+1000;
        code.append(numIdx);

        return code.toString();
    }

    public void sendMail(String email, String code){
        String subject = "제목: 그루터기 회원 가입 인증 절차";
        String text = "회원 가입을 위한 인증번호는 "+ code + " 입니다. " +
                "제한 시간 3분 이내에 인증번호를 입력해주세요.";

        try{
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            helper.setSubject(subject);
            helper.setText(text,true);
            helper.setTo(email);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        String key = prefix + email;
        redisClient.setValue(key, code, 3L); // key(email), value(code), timeout
    }
}
