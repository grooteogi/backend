package grooteogi.service;

import grooteogi.domain.User;
import grooteogi.domain.UserInfo;
import grooteogi.dto.ProfileDto;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.repository.UserRepository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProfileService {
  private final UserRepository userRepository;

  public User getUserInfo(Integer userId) {
    Optional<User> user = userRepository.findUserById(userId);
    if (user.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.USERINFO_NOT_FOUND_EXCEPTION);
    }
    return user.get();
  }

  public User modifyUserInfo(Integer userId, ProfileDto profileDto) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.USER_NOT_FOUND_EXCEPTION);
    }
    UserInfo info = user.get().getUserInfo();
    if (info != null) {
      BeanUtils.copyProperties(profileDto, info);
      info.setModified(Timestamp.valueOf(LocalDateTime.now()));
      user.get().setUserInfo(info);
    } else {
      UserInfo userInfo = new UserInfo();
      BeanUtils.copyProperties(profileDto, userInfo);
      userInfo.setModified(Timestamp.valueOf(LocalDateTime.now()));
      user.get().setUserInfo(userInfo);
    }

    if (!user.get().getNickname().equals(profileDto.getNickname())
        && userRepository.existsByNickname(profileDto.getNickname())) {
      throw new ApiException(ApiExceptionEnum.DUPLICATION_VALUE_EXCEPTION);
    }
    user.get().setNickname(profileDto.getNickname());

    if (!profileDto.getPassword().equals("")) {
      confirmPasswordVerification(profileDto.getPassword());
      user.get().setPassword(profileDto.getPassword());
    }
    user.get().setModified(Timestamp.valueOf(LocalDateTime.now()));
    return userRepository.save(user.get());
  }

  private void confirmPasswordVerification(String password) {
    Pattern pwPattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$");
    Matcher pwMatcher = pwPattern.matcher(password);

    if (!pwMatcher.find()) {
      throw new ApiException(ApiExceptionEnum.PASSWORD_VALUE_EXCEPTION);
    }

  }
}
