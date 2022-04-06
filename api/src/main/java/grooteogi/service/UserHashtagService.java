package grooteogi.service;

import grooteogi.domain.Hashtag;
import grooteogi.domain.User;
import grooteogi.domain.UserHashtag;
import grooteogi.dto.UserHashtagDto;
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
        userhashtag.setUser(user.get());
        Optional<Hashtag> hashtag =
            this.hashtagRepository.findById(userHashtagDto.getHashtagId()[i]);

        //데이터 처리
        hashtag.get().setCount(hashtag.get().getCount() + 1);
        userhashtag.setHashtag(hashtag.get());
        userhashtag.setRegistered(Timestamp.valueOf(LocalDateTime.now()));

        //리턴
        this.userHashtagRepository.save(userhashtag);

      }
      return this.userHashtagRepository.findByUserId(userHashtagDto.getUserId());
    }

    return null;
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

    return null;
  }

  public List<UserHashtag> getAllUserHashtag() {
    return this.userHashtagRepository.findAll();
  }

  public List<UserHashtag> getUserHashtag(int userId) {
    return this.userHashtagRepository.findByUserId(userId);
  }

}
