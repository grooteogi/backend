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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProfileService {

  private final UserRepository userRepository;

  public User getUserProfile(Integer userId) {
    Optional<User> user = userRepository.findProfileById(userId);
    if (user.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.USER_NOT_FOUND_EXCEPTION);
    }
    return user.get();
  }

  public User modifyUserProfile(Integer userId, ProfileDto profileDto) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.USER_NOT_FOUND_EXCEPTION);
    }
    UserInfo userInfo = user.get().getUserInfo();
    if (userInfo == null) {
      userInfo = new UserInfo();
    }

    BeanUtils.copyProperties(profileDto, userInfo);
    userInfo.setModified(Timestamp.valueOf(LocalDateTime.now()));
    user.get().setUserInfo(userInfo);

    if (!user.get().getNickname().equals(profileDto.getNickname())
        && userRepository.existsByNickname(profileDto.getNickname())) {
      throw new ApiException(ApiExceptionEnum.DUPLICATION_VALUE_EXCEPTION);
    }
    user.get().setNickname(profileDto.getNickname());

    user.get().setModified(Timestamp.valueOf(LocalDateTime.now()));
    return userRepository.save(user.get());
  }
}
