package grooteogi.service;

import grooteogi.domain.User;
import grooteogi.domain.UserInfo;
import grooteogi.dto.AuthDto;
import grooteogi.dto.ProfileDto;
import grooteogi.dto.UserDto;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.mapper.UserMapper;
import grooteogi.repository.UserRepository;
import grooteogi.utils.Validator;
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

  public User getUserByEmail(AuthDto.Request request) {

    User requestUser = UserMapper.INSTANCE.toEntity(request);

    Optional<User> user = userRepository.findByEmail(requestUser.getEmail());

    if (user.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.USER_NOT_FOUND_EXCEPTION);
    }

    return user.get();
  }

  public boolean isExistEmail(String email) {
    return userRepository.existsByEmail(email);
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
    user.get().setUserInfo(userInfo);

    if (!user.get().getNickname().equals(request.getNickname())
        && userRepository.existsByNickname(request.getNickname())) {
      throw new ApiException(ApiExceptionEnum.DUPLICATION_VALUE_EXCEPTION);
    }
    user.get().setNickname(request.getNickname());

    return userRepository.save(user.get());
  }

  public void modifyUserPw(Integer userId, UserDto.PasswordRequest request) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.USER_NOT_FOUND_EXCEPTION);
    }

    if (passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
      throw new ApiException(ApiExceptionEnum.DUPLICATION_VALUE_EXCEPTION);
    }

    validator.confirmPasswordVerification(request.getPassword());
    user.get().setPassword(passwordEncoder.encode(request.getPassword()));
    userRepository.save(user.get());
  }
}
