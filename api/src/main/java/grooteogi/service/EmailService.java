package grooteogi.service;

import grooteogi.dto.EmailCodeRequest;
import grooteogi.dto.EmailRequest;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.repository.UserRepository;
import grooteogi.utils.RedisClient;
import java.util.Random;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

  @Value("${spring.mail.username}")
  private String mailServer;

  private final UserRepository userRepository;
  private final JavaMailSender javaMailSender;
  private final RedisClient redisClient;

  private final String prefix = "email_verify";

  private boolean isExist(EmailRequest email) {
    return userRepository.existsByEmail(email.getEmail());
  }

  public void createEmailVerification(EmailRequest email) {
    if (isExist(email)) {
      throw new ApiException(ApiExceptionEnum.EMAIL_DUPLICATION_EXCEPTION);
    }
    String code = createCode();
    sendMail(email.getEmail(), code);
  }

  public void confirmEmailVerification(EmailCodeRequest emailCodeRequest) {
    String key = prefix + emailCodeRequest.getEmail();
    String value = redisClient.getValue(key);
    if (value == null || !value.equals(emailCodeRequest.getCode())) {
      throw new ApiException(ApiExceptionEnum.EXPIRED_TOKEN_EXCEPTION);
    }
  }

  private static String createCode() {
    Random random = new Random();
    StringBuilder code = new StringBuilder();

    for (int i = 0; i < 3; i++) {
      int idx = random.nextInt(25) + 65;
      code.append((char) idx);
    }
    int numIdx = random.nextInt(9999) + 1000;
    code.append(numIdx);

    return code.toString();
  }

  public void sendMail(String email, String code) {
    String subject = "제목: 그루터기 회원 가입 인증 절차";
    String text = "회원 가입을 위한 인증번호는 " + code + " 입니다. "
        + "제한 시간 3분 이내에 인증번호를 입력해주세요.";

    try {
      MimeMessage mimeMessage = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
      helper.setFrom(mailServer);
      helper.setTo(email);
      helper.setSubject(subject);
      helper.setText(text, true);
      javaMailSender.send(mimeMessage);
    } catch (MessagingException e) {
      e.printStackTrace();
    }
    String key = prefix + email;
    redisClient.setValue(key, code, 3L);
  }
}
