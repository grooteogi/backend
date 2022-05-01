package grooteogi.service;

import grooteogi.domain.Hashtag;
import grooteogi.domain.User;
import grooteogi.domain.UserHashtag;
import grooteogi.dto.UserHashtagDto;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.repository.HashtagRepository;
import grooteogi.repository.UserHashtagRepository;
import grooteogi.repository.UserRepository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserHashtagService {

  private final UserHashtagRepository userHashtagRepository;
  private final UserRepository userRepository;
  private final HashtagRepository hashtagRepository;


  public List<UserHashtag> getAllUserHashtag() {
    return this.userHashtagRepository.findAll();
  }

  public List<UserHashtag> getUserHashtag(int userId) {
    List<UserHashtag> userHashtag = userHashtagRepository.findByUserId(userId);
    if (userHashtag.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.USER_NOT_FOUND_EXCEPTION);
    }
    return this.userHashtagRepository.findByUserId(userId);
  }

  public List<UserHashtag> saveUserHashtag(UserHashtagDto userHashtagDto) {
    Optional<User> user = this.userRepository.findById(userHashtagDto.getUserId());
    if (user.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.USER_NOT_FOUND_EXCEPTION);
    }

    String[] hashtags = userHashtagDto.getHashtags();

    Arrays.stream(hashtags).forEach(name -> {
      UserHashtag userHashtag = new UserHashtag();
      Hashtag hashtag = this.hashtagRepository.findByTag(name);
      if (hashtag == null) {
        throw new ApiException(ApiExceptionEnum.HASHTAG_NOT_FOUND_EXCEPTION);
      }

      if (this.userHashtagRepository.findByUserIdAndHashtagId(
          user.get().getId(), hashtag.getId()) != null) {
        throw new ApiException(ApiExceptionEnum.DUPLICATION_VALUE_EXCEPTION);
      }

      userHashtag.setUser(user.get());
      hashtag.setCount(hashtag.getCount() + 1);
      userHashtag.setHashtag(hashtag);
      userHashtag.setRegistered(Timestamp.valueOf(LocalDateTime.now()));

      this.userHashtagRepository.save(userHashtag);
    });

    return this.userHashtagRepository.findByUserId(userHashtagDto.getUserId());
  }

  public List<UserHashtag> modifyUserHashtag(UserHashtagDto userHashtagDto) {
    Optional<User> user = this.userRepository.findById(userHashtagDto.getUserId());
    String[] hashtags = userHashtagDto.getHashtags();

    if (user.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.USER_NOT_FOUND_EXCEPTION);
    }
    Arrays.stream(hashtags).forEach(name -> {
      if (this.hashtagRepository.findByTag(name) == null) {
        throw new ApiException(ApiExceptionEnum.HASHTAG_NOT_FOUND_EXCEPTION);
      }
    });

    List<Hashtag> modifyHashtagList = new ArrayList<>();

    Arrays.stream(hashtags).forEach(name ->
        modifyHashtagList.add(this.hashtagRepository.findByTag(name)));

    List<UserHashtag> beforeUserHashtagList =
        this.userHashtagRepository.findByUserId(userHashtagDto.getUserId());

    beforeUserHashtagList.forEach(beforeUserHashtag -> {
      Hashtag beforeHashtag = beforeUserHashtag.getHashtag();
      if (modifyHashtagList.contains(beforeHashtag)) {
        modifyHashtagList.remove(beforeHashtag);
      } else {
        beforeHashtag.setCount(beforeHashtag.getCount() - 1);
        this.userHashtagRepository.delete(beforeUserHashtag);
      }
    });

    modifyHashtagList.forEach(hashtag -> {
      UserHashtag userHashtag = new UserHashtag();
      hashtag.setCount(hashtag.getCount() + 1);

      userHashtag.setUser(user.get());
      userHashtag.setHashtag(hashtag);
      userHashtag.setRegistered(Timestamp.valueOf(LocalDateTime.now()));

      this.userHashtagRepository.save(userHashtag);
    });
    return this.userHashtagRepository.findByUserId(user.get().getId());
  }

  public List<UserHashtag> deleteUserHashtag(int userId, String[] hashtags) {
    if (hashtags == null) {
      throw new ApiException(ApiExceptionEnum.BAD_REQUEST_EXCEPTION);
    }

    Optional<User> user = this.userRepository.findById(userId);
    if (user.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.USER_NOT_FOUND_EXCEPTION);
    }

    Arrays.stream(hashtags).forEach(name -> {
      Hashtag hashtag = this.hashtagRepository.findByTag(name);
      UserHashtag userHashtag = this.userHashtagRepository.findByUserIdAndHashtagId(userId,
          hashtag.getId());

      if (this.userHashtagRepository.findByUserIdAndHashtagId(userId, hashtag.getId()) == null) {
        throw new ApiException(ApiExceptionEnum.USERHASHTAG_NOT_FOUND_EXCEPTION);
      }

      hashtag.setCount(hashtag.getCount() - 1);
      this.userHashtagRepository.delete(userHashtag);
    });

    return this.userHashtagRepository.findByUserId(userId);
  }
}
