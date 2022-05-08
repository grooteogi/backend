package grooteogi.service;

import grooteogi.domain.User;
import grooteogi.domain.UserInfo;
import grooteogi.dto.ProfileDto;
import grooteogi.dto.UserDto.Password;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.repository.UserRepository;
import grooteogi.utils.Validator;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final Validator validator;

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

  public boolean isExistEmail(String address) {
    return userRepository.existsByEmail(address);
  }

  public User getUserProfile(Integer userId) {
    Optional<User> user = userRepository.findProfileById(userId);
    if (user.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.USER_NOT_FOUND_EXCEPTION);
    }
    return user.get();
  }

  public User modifyUserProfile(Integer userId, ProfileDto.Request request) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.USER_NOT_FOUND_EXCEPTION);
    }
    UserInfo userInfo = user.get().getUserInfo();
    if (userInfo == null) {
      userInfo = new UserInfo();
    }

    BeanUtils.copyProperties(request, userInfo);
    userInfo.setModified(Timestamp.valueOf(LocalDateTime.now()));
    user.get().setUserInfo(userInfo);

    if (!user.get().getNickname().equals(request.getNickname())
        && userRepository.existsByNickname(request.getNickname())) {
      throw new ApiException(ApiExceptionEnum.DUPLICATION_VALUE_EXCEPTION);
    }
    user.get().setNickname(request.getNickname());

    user.get().setModified(Timestamp.valueOf(LocalDateTime.now()));
    return userRepository.save(user.get());
  }

  public void modifyUserPw(Integer userId, Password request) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.USER_NOT_FOUND_EXCEPTION);
    }

    if (passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
      throw new ApiException(ApiExceptionEnum.DUPLICATION_VALUE_EXCEPTION);
    }

    validator.confirmPasswordVerification(request.getPassword());
    user.get().setPassword(passwordEncoder.encode(request.getPassword()));
    user.get().setModified(Timestamp.valueOf(LocalDateTime.now()));
    userRepository.save(user.get());
  }
}
