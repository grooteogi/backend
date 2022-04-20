package grooteogi.service;

import grooteogi.domain.User;
import grooteogi.dto.LoginDto;
import grooteogi.dto.Token;
import grooteogi.dto.UserDto;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.repository.UserRepository;
import grooteogi.utils.JwtProvider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final JwtProvider jwtProvider;
  private final PasswordEncoder passwordEncoder;

  public List<User> getAllUser() {
    return userRepository.findAll();
  }

  public User getUser(int userId) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.USER_NOT_FOUND_EXCEPTION);
    }
    return user.get();
  }

  public User getUserByEmail(String email) {
    Optional<User> user = userRepository.findByEmail(email);
    if (user.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.USER_NOT_FOUND_EXCEPTION);
    }
    return user.get();
  }

  public void withdrawal(int userId) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.USER_NOT_FOUND_EXCEPTION);
    }
    userRepository.delete(user.get());
  }

  public User register(UserDto userDto) {
    Optional<User> user = userRepository.findByEmail(userDto.getEmail());
    if (user.isPresent()) {
      throw new ApiException(ApiExceptionEnum.EMAIL_DUPLICATION_EXCEPTION);
    }
    userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
    userDto.setNickname("groot");

    User registerUser = registerDto(userDto);
    if (registerUser.getNickname().equals("groot")) {
      registerUser.setNickname(registerUser.getNickname() + "-" + registerUser.getId());
    }
    return userRepository.save(registerUser);
  }

  public Token login(LoginDto loginDto) {
    User user = getUserByEmail(loginDto.getEmail());
    if (passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
      return generateToken(user.getId(), loginDto.getEmail());
    } else {
      throw new ApiException(ApiExceptionEnum.LOGIN_FAIL_EXCEPTION);
    }
  }

  public Map<String, Object> oauth(UserDto userDto) {
    Map<String, Object> result = new HashMap<String, Object>();

    Optional<User> userEmail = userRepository.findByEmail(userDto.getEmail());
    User user;
    if (userEmail.isEmpty()) {
      user = registerDto(userDto);
    } else {
      user = userEmail.get();
    }

    if (user.getType().equals(userDto.getType())) {
      result.put("token", generateToken(user.getId(), user.getEmail()));
    } else {
      throw new ApiException(ApiExceptionEnum.LOGIN_FAIL_EXCEPTION);
    }

    result.put("user", user);
    return result;
  }

  private User registerDto(UserDto userDto) {
    User user = new User();
    BeanUtils.copyProperties(userDto, user);

    return userRepository.save(user);
  }

  private Token generateToken(int id, String email) {
    Token token = new Token();
    token.setAccessToken(jwtProvider.generateAccessToken(id, email));
    token.setRefreshToken(jwtProvider.generateRefreshToken(id, email));

    return token;
  }

  // TODO change public to private
  public Map verify(String authorizationHeader) {
    return jwtProvider.verifyToken(authorizationHeader);
  }

  public Map refresh(String authorizationHeader, String refreshToken) {
    return jwtProvider.refreshToken(authorizationHeader, refreshToken);
  }
}
