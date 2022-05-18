package grooteogi.service;

import static grooteogi.enums.JwtExpirationEnum.REDIS_TOKEN_EXPIRATION_TIME;

import grooteogi.domain.User;
import grooteogi.dto.AuthDto;
import grooteogi.dto.auth.EmailCodeDto;
import grooteogi.dto.auth.Token;
import grooteogi.dto.auth.UserDto;
import grooteogi.enums.LoginType;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.repository.UserRepository;
import grooteogi.utils.EmailClient;
import grooteogi.utils.JwtProvider;
import grooteogi.utils.RedisClient;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final String prefix = "email_verify";

  private final JwtProvider jwtProvider;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final EmailClient emailClient;
  private final RedisClient redisClient;

  public Token login(User user, AuthDto.Request request) {

    if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      Token token = generateToken(user.getId(), user.getEmail());
      session(token.getAccessToken(), user.getId());
      return token;
    } else {
      throw new ApiException(ApiExceptionEnum.LOGIN_FAIL_EXCEPTION);
    }
  }

  public User register(AuthDto.Request request) {
    Optional<User> user = userRepository.findByEmail(request.getEmail());
    if (user.isPresent()) {
      throw new ApiException(ApiExceptionEnum.EMAIL_DUPLICATION_EXCEPTION);
    }

    String encodedPassword = passwordEncoder.encode(request.getPassword());

    User createdUser = User.builder()
        .email(request.getEmail())
        .password(encodedPassword)
        .type(LoginType.GENERAL)
        .nickname("groot")
        .build();


    User registerUser = userRepository.save(createdUser);

    if (registerUser.getNickname().equals("groot")) {
      registerUser.setNickname(registerUser.getNickname() + "-" + registerUser.getId());
    }

    return userRepository.save(registerUser);

  }

  private User registerDto(UserDto userDto) {
    User user = new User();
    BeanUtils.copyProperties(userDto, user);

    return userRepository.save(user);
  }

  public void withdrawal(int userId) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.USER_NOT_FOUND_EXCEPTION);
    }
    userRepository.delete(user.get());
  }

  private Token generateToken(int id, String email) {
    Token token = new Token();
    token.setAccessToken(jwtProvider.generateAccessToken(id, email));
    token.setRefreshToken(jwtProvider.generateRefreshToken(id, email));

    return token;
  }

  public void sendVerifyEmail(String email) {
    String code = emailClient.createCode();
    emailClient.send(email, code);

    String key = prefix + email;
    redisClient.setValue(key, code, 3L);
  }

  public void checkVerifyEmail(EmailCodeDto.Request emailCodeRequest) {
    String key = prefix + emailCodeRequest.getEmail();
    String value = redisClient.getValue(key);
    if (value == null || !value.equals(emailCodeRequest.getCode())) {
      throw new ApiException(ApiExceptionEnum.INVALID_CODE_EXCEPTION);
    }
  }

  public Map<String, Object> oauth(UserDto userDto) {
    Map<String, Object> result = new HashMap<>();

    Optional<User> userEmail = userRepository.findByEmail(userDto.getEmail());
    User user;
    if (userEmail.isEmpty()) {
      user = registerDto(userDto);
    } else {
      user = userEmail.get();
    }

    if (user.getType().equals(userDto.getType())) {
      Token token = generateToken(user.getId(), user.getEmail());
      session(token.getAccessToken(), user.getId());
      result.put("token", token);
    } else {
      throw new ApiException(ApiExceptionEnum.LOGIN_FAIL_EXCEPTION);
    }

    result.put("user", user);
    return result;
  }

  private void session(String token, int id) {
    redisClient.setValue(token, Integer.toString(id), REDIS_TOKEN_EXPIRATION_TIME.getValue());
  }
}
