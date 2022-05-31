package grooteogi.utils;

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
public class EmailClient {

  private static final String subject = "제목: 그루터기 회원 가입 인증 절차";

  private final JavaMailSender javaMailSender;

  @Value("${spring.mail.username}")
  private String mailServer;

  public static String createCode() {
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

  private String createMessage(String code) {
    return "회원 가입을 위한 인증번호는 " + code + " 입니다. " + "제한 시간 3분 이내에 인증번호를 입력해주세요.";
  }

  public void send(String email, String code) {
    try {
      MimeMessage mimeMessage = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
      helper.setFrom(mailServer);
      helper.setTo(email);
      helper.setSubject(subject);
      helper.setText(createMessage(code), true);
      javaMailSender.send(mimeMessage);
    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }
}
