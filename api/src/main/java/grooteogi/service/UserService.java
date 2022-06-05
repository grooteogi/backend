package grooteogi.service;

import grooteogi.domain.User;
import grooteogi.domain.UserInfo;
import grooteogi.dto.AuthDto;
import grooteogi.dto.ProfileDto;
import grooteogi.dto.UserDto;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.mapper.UserInfoMapper;
import grooteogi.mapper.UserMapper;
import grooteogi.repository.UserInfoRepository;
import grooteogi.repository.UserRepository;
import grooteogi.utils.Validator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserInfoRepository userInfoRepository;
  private final PasswordEncoder passwordEncoder;
  private final Validator validator;

  public List<User> getAllUser() {
    return userRepository.findAll();
  }

  public User getUser(int userId) {
    Optional<User> user = userRepository.findById(userId);
    user.orElseThrow(() -> new ApiException(ApiExceptionEnum.USER_NOT_FOUND_EXCEPTION));
    return user.get();
  }

  public User getUserByEmail(AuthDto.Request request) {

    User requestUser = UserMapper.INSTANCE.toEntity(request);

    Optional<User> user = userRepository.findByEmail(requestUser.getEmail());

    user.orElseThrow(() -> new ApiException(ApiExceptionEnum.USER_NOT_FOUND_EXCEPTION));

    return user.get();
  }

  public boolean isExistEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  public ProfileDto.Response getUserProfile(Integer userId) {
    Optional<User> user = userRepository.findById(userId);
    user.orElseThrow(() -> new ApiException(ApiExceptionEnum.USER_NOT_FOUND_EXCEPTION));

    Optional<UserInfo> userInfo = Optional.ofNullable(user.get().getUserInfo());

    userInfo.orElseThrow(() -> new ApiException(ApiExceptionEnum.USERINFO_NOT_FOUND_EXCEPTION));

    return UserInfoMapper.INSTANCE.toResponseDto(user.get(), userInfo.get());
  }

  public void modifyUserProfile(Integer userId, ProfileDto.Request request) {
    Optional<User> user = userRepository.findById(userId);
    user.orElseThrow(() -> new ApiException(ApiExceptionEnum.USER_NOT_FOUND_EXCEPTION));

    if (!user.get().getNickname().equals(request.getNickname())
        && userRepository.existsByNickname(request.getNickname())) {
      throw new ApiException(ApiExceptionEnum.DUPLICATION_VALUE_EXCEPTION);
    }

    UserInfo userInfo = userInfoRepository.save(UserInfoMapper.INSTANCE.toEntity(request));

    User modifiedUser = UserMapper.INSTANCE.toModify(user.get(), request.getNickname(), userInfo);

    userRepository.save(modifiedUser);
  }

  public void modifyUserPw(Integer userId, UserDto.PasswordRequest request) {
    Optional<User> user = userRepository.findById(userId);
    user.orElseThrow(() -> new ApiException(ApiExceptionEnum.USER_NOT_FOUND_EXCEPTION));

    if (!passwordEncoder.matches(request.getCurrentPassword(), user.get().getPassword())) {
      throw new ApiException(ApiExceptionEnum.PASSWORD_DISCORD_EXCEPTION);
    }

    if (request.getCurrentPassword().equals(request.getNewPassword())) {
      throw new ApiException(ApiExceptionEnum.DUPLICATION_VALUE_EXCEPTION);
    }

    validator.confirmPasswordVerification(request.getNewPassword());
    user.get().setPassword(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user.get());
  }

  public AuthDto.Response getAuthResponse(User user) {
    return UserMapper.INSTANCE.toResponseDto(user, user.getUserInfo());
  }
}
