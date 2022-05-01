package grooteogi.service;

import grooteogi.dto.auth.EmailCodeDto;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.repository.UserRepository;
import grooteogi.utils.EmailClient;
import grooteogi.utils.RedisClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

  private final UserRepository userRepository;
  private final EmailClient emailClient;
  private final RedisClient redisClient;

  private final static String prefix = "email_verify";

  private boolean isExist(String email) {
    return userRepository.existsByEmail(email);
  }

  public void sendVerifyEmail(String email) {
    if (isExist(email)) {
      throw new ApiException(ApiExceptionEnum.EMAIL_DUPLICATION_EXCEPTION);
    }
    String code = emailClient.createCode();
    emailClient.send(email, code);

    String key = prefix + email;
    redisClient.setValue(key, code, 3L);
  }

  public void checkVerifyEmail(EmailCodeDto emailCodeRequest) {
    String key = prefix + emailCodeRequest.getEmail();
    String value = redisClient.getValue(key);
    if (value == null || !value.equals(emailCodeRequest.getCode())) {
      throw new ApiException(ApiExceptionEnum.EXPIRED_TOKEN_EXCEPTION);
    }
  }

}
