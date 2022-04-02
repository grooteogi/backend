package grooteogi.service;

import grooteogi.domain.Hashtag;
import grooteogi.domain.User;
import grooteogi.domain.UserHashtag;
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

  public List<UserHashtag> saveUserHashtag(int userId, int[] hashtagId) {
    if (hashtagId != null) {
      for (int i = 0; i < hashtagId.length; i++) {
        UserHashtag userhashtag = new UserHashtag();
        Optional<User> user = this.userRepository.findById(userId);
        userhashtag.setUser(user.get());
        Optional<Hashtag> hashtag = this.hashtagRepository.findById(hashtagId[i]);
        userhashtag.setHashTag(hashtag.get());
        userhashtag.setRegistered(Timestamp.valueOf(LocalDateTime.now()));
        this.userHashtagRepository.save(userhashtag);
      }
      return this.userHashtagRepository.getAllUserHashtagBy(userId);
    }

    return null;
  }

  public List<UserHashtag> deleteUserHashtag(int userId, int[] hashtagId) {
    if (hashtagId != null) {
      for (int i = 0; i < hashtagId.length; i++) {
        UserHashtag userHashtag = this.userHashtagRepository.getUserHashtagBy(userId,
            hashtagId[0]);
        this.userHashtagRepository.delete(userHashtag);
      }
      return this.userHashtagRepository.getAllUserHashtagBy(userId);
    }

    return null;
  }

  public List<UserHashtag> getAllUserHashtag() {
    return this.userHashtagRepository.findAll();
  }

  public List<UserHashtag> getUserHashtag(int userId) {
    return this.userHashtagRepository.getAllUserHashtagBy(userId);
  }


}
