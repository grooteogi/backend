package grooteogi.service;

import grooteogi.domain.User;
import grooteogi.dto.LoginDto;
import grooteogi.dto.Token;
import grooteogi.dto.UserDto;
import grooteogi.enums.LoginType;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.repository.UserRepository;
import grooteogi.utils.JwtProvider;
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
    switch (userDto.getType()) {
      case GENERAL:
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        break;

      default:
        userDto.setPassword(userDto.getToken());
        break;
    }
    if (userDto.getNickname() == null) {
      userDto.setNickname("groot");
    }

    User user = new User();
    BeanUtils.copyProperties(userDto, user);

    User registerUser = userRepository.save(user);
    registerUser.setNickname(registerUser.getNickname() + "-" + registerUser.getId());
    return userRepository.save(registerUser);
  }

  public Token login(LoginDto loginDto) {
    User user = getUserByEmail(loginDto.getEmail());
    if (loginDto.getPassword().isBlank() && user.getType() != LoginType.GENERAL) {
      return generateToken(user.getId(), loginDto.getEmail());
    } else if (passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
      return generateToken(user.getId(), loginDto.getEmail());
    } else {
      throw new ApiException(ApiExceptionEnum.LOGIN_FAIL_EXCEPTION);
    }
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
