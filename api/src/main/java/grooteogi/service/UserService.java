package grooteogi.service;

import grooteogi.domain.EmailCodeRequest;
import grooteogi.domain.EmailRequest;
import grooteogi.domain.User;
import grooteogi.domain.UserDto;
import grooteogi.repository.UserRepository;
import java.util.List;
import java.util.Random;

import grooteogi.utils.RedisClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;


@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  private final JavaMailSender javaMailSender;
  private final RedisClient redisClient;

  public List<User> getAllUser() {
    return userRepository.findAll();
  }

  private static String createcode() {
    Random random = new Random();
    String code = "";
    for(int i =0; i< 3; i++){
      int idx = random.nextInt(25)+65;
      code+=(char)idx;
    }
    int numidx = random.nextInt(9999)+1000;

    code+=numidx;
    return code;
  }

  // duplicate email
  public boolean genarateEmailVerify(EmailRequest email) {
    if(userRepository.existsByEmail(email.getEmail())){ // 중복
      return false;
    }else {
      // 이메일 인증코드 전송하기
      String code = createcode();
      sendMail(email.getEmail(),code);
      return true;
    }
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
    String prefix = "email_verify";
    String key = prefix + email;
    redisClient.setValue(key, code, 1L); // key(email), value(code), timeout
  }
  public boolean confirmEmailVerify(EmailCodeRequest emailCodeRequest)  {
    // 보낸 인증코드와 맞는지 확인하기
    String prefix = "email_verify";
    String key = prefix + emailCodeRequest.getEmail();
    String value = redisClient.getValue(key);
    if(value != null) {
      return value.equals(emailCodeRequest.getCode());
    } else return false;
  }
}
