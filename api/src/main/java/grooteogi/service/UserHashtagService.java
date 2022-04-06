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

  public List<UserHashtag> saveUserHashtag(UserHashtagDto userHashtagDto) {
    if (userHashtagDto.getHashtagId() != null) {
      for (int i = 0; i < userHashtagDto.getHashtagId().length; i++) {
        //변수 정의
        UserHashtag userhashtag = new UserHashtag();
        Optional<User> user = this.userRepository.findById(userHashtagDto.getUserId());
        Optional<Hashtag> hashtag =
            this.hashtagRepository.findById(userHashtagDto.getHashtagId()[i]);

        //예외 처리
        if (this.userHashtagRepository.findByUserIdAndHashtagId(
            user.get().getId(), hashtag.get().getId()) != null) {
          throw new ApiException(ApiExceptionEnum.DUPLICATION_VALUE_EXCEPTION);
        }

        //데이터 처리
        userhashtag.setUser(user.get());
        hashtag.get().setCount(hashtag.get().getCount() + 1);
        userhashtag.setHashtag(hashtag.get());
        userhashtag.setRegistered(Timestamp.valueOf(LocalDateTime.now()));




        //리턴
        this.userHashtagRepository.save(userhashtag);

      }
      return this.userHashtagRepository.findByUserId(userHashtagDto.getUserId());
    }

    throw new ApiException(ApiExceptionEnum.BAD_REQUEST_EXCEPTION);
  }

  public List<UserHashtag> deleteUserHashtag(int userId, int[] hashtagId) {
    if (hashtagId != null) {
      for (int i = 0; i < hashtagId.length; i++) {

        //변수 정의
        UserHashtag userHashtag = this.userHashtagRepository.findByUserIdAndHashtagId(userId,
            hashtagId[i]);
        Optional<Hashtag> hashtag = this.hashtagRepository.findById(hashtagId[i]);

        //데이터 처리
        hashtag.get().setCount(hashtag.get().getCount() - 1);

        //리턴
        this.userHashtagRepository.delete(userHashtag);

      }
      return this.userHashtagRepository.findByUserId(userId);
    }

    throw new ApiException(ApiExceptionEnum.BAD_REQUEST_EXCEPTION);
  }

  public List<UserHashtag> getAllUserHashtag() {
    return this.userHashtagRepository.findAll();
  }

  public List<UserHashtag> getUserHashtag(int userId) {
    Optional<UserHashtag> userHashtag = userHashtagRepository.findById(userId);
    if (userHashtag.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.USER_NOT_FOUND_EXCEPTION);
    }
    return this.userHashtagRepository.findByUserId(userId);
  }

}
