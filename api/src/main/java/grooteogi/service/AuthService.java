package grooteogi.service;

import grooteogi.domain.User;
import grooteogi.domain.UserInfo;
import grooteogi.dto.AuthDto;
import grooteogi.dto.auth.OauthDto;
import grooteogi.dto.auth.Token;
import grooteogi.enums.LoginType;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.mapper.UserMapper;
import grooteogi.repository.UserInfoRepository;
import grooteogi.repository.UserRepository;
import grooteogi.utils.EmailClient;
import grooteogi.utils.JwtProvider;
import grooteogi.utils.RedisClient;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  @Value("${spring.jwt.redis_token_expiration_time}")
  private Long redisTokenExpirationTime;

  private final String prefix = "email_verify";
  private final String nickname = "groot";

  private final JwtProvider jwtProvider;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final UserInfoRepository userInfoRepository;
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

  public AuthDto.Response register(AuthDto.Request request) {
    Optional<User> user = userRepository.findByEmail(request.getEmail());
    if (user.isPresent()) {
      throw new ApiException(ApiExceptionEnum.EMAIL_DUPLICATION_EXCEPTION);
    }

    request.setPassword(passwordEncoder.encode(request.getPassword()));

    User cuser = UserMapper.INSTANCE.toEntity(request, LoginType.GENERAL, nickname);
    User registerUser = userRepository.save(cuser);

    if (registerUser.getNickname().equals(nickname)) {
      registerUser.setNickname(registerUser.getNickname() + "-" + registerUser.getId());
    }

    UserInfo userInfo = userInfoRepository.save(new UserInfo());

    User modifiedUser = UserMapper.INSTANCE.toModify(registerUser,
        registerUser.getNickname(), userInfo);
    userRepository.save(modifiedUser);

    return UserMapper.INSTANCE.toResponseDto(modifiedUser, modifiedUser.getUserInfo());
  }

  private User registerDto(OauthDto oauthDto) {
    User user = new User();
    BeanUtils.copyProperties(oauthDto, user);

    return userRepository.save(user);
  }

  public void withdrawal(int userId) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.USER_NOT_FOUND_EXCEPTION);
    }
    userRepository.delete(user.get());
  }

  public Token generateToken(int id, String email) {
    Token token = new Token();
    token.setAccessToken(jwtProvider.generateAccessToken(id, email));
    token.setRefreshToken(jwtProvider.generateRefreshToken(id, email));

    return token;
  }

  public void sendVerifyEmail(String email) {
    String code = EmailClient.createCode();
    emailClient.send(email, code);

    String key = prefix + email;
    redisClient.setValue(key, code, 3L);
  }

  public void checkVerifyEmail(AuthDto.CheckEmailRequest request) {
    String key = prefix + request.getEmail();
    Optional<String> value = Optional.ofNullable(redisClient.getValue(key));

    value.orElseThrow(() -> new ApiException(ApiExceptionEnum.TIME_OUT_EXCEPTION));

    if (!value.get().equals(request.getCode())) {
      throw new ApiException(ApiExceptionEnum.INVALID_CODE_EXCEPTION);
    }
  }

  public User oauth(OauthDto oauthDto) {
    Optional<User> userEmail = userRepository.findByEmail(oauthDto.getEmail());
    User user;
    if (userEmail.isEmpty()) {
      user = registerDto(oauthDto);
    } else {
      user = userRepository.save(userEmail.get());
    }

    if (!user.getType().equals(oauthDto.getType())) {
      throw new ApiException(ApiExceptionEnum.LOGIN_FAIL_EXCEPTION);
    }

    return user;
  }

  private void session(String token, int id) {
    redisClient.setValue(token, Integer.toString(id), redisTokenExpirationTime);
  }

  public AuthDto.Response getAuthResponse(User user) {
    return UserMapper.INSTANCE.toResponseDto(user, user.getUserInfo());
  }
}
