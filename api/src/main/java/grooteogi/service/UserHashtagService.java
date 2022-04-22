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
    if (userHashtagDto.getHashtagIds() != null) {

      for (int i = 0; i < userHashtagDto.getHashtagIds().length; i++) {
        if (this.hashtagRepository.findById(userHashtagDto.getHashtagIds()[i]).isEmpty()) {
          throw new ApiException(ApiExceptionEnum.HASHTAG_NOT_FOUND_EXCEPTION);
        }

        //변수 정의
        UserHashtag userhashtag = new UserHashtag();
        Optional<User> user = this.userRepository.findById(userHashtagDto.getUserId());
        Optional<Hashtag> hashtag =
            this.hashtagRepository.findById(userHashtagDto.getHashtagIds()[i]);

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

    throw new ApiException(ApiExceptionEnum.HASHTAG_NOT_FOUND_EXCEPTION);
  }

  public List<UserHashtag> modifyUserHashtag(UserHashtagDto userHashtagDto) {
    Optional<User> user = this.userRepository.findById(userHashtagDto.getUserId());

    //예외처리
    if (user.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.USER_NOT_FOUND_EXCEPTION);
    }
    if (userHashtagDto.getHashtagIds() == null) {
      throw new ApiException(ApiExceptionEnum.BAD_REQUEST_EXCEPTION);
    }

    for (int hashtagId : userHashtagDto.getHashtagIds()) {
      if (this.hashtagRepository.findById(hashtagId).isEmpty()) {
        throw new ApiException(ApiExceptionEnum.HASHTAG_NOT_FOUND_EXCEPTION);
      }
    }

    //기존 유저 해시태그 List
    List<UserHashtag> beforeUserHashtagList =
        this.userHashtagRepository.findByUserId(userHashtagDto.getUserId());

    //수정할 해시태그 List로 저장
    List<Hashtag> modifyHashtagList = new ArrayList<>();
    for (int hashtagId : userHashtagDto.getHashtagIds()) {
      modifyHashtagList.add(this.hashtagRepository.findById(hashtagId).get());
    }

    for (UserHashtag beforeUserHashtag : beforeUserHashtagList) {
      //수정할 해시태그 리스트에 기존 유저 해시태그가 포함되어 있다면 수정할 해시태그 리스트에서 제거
      //중복 저장을 피하기 위함
      Hashtag beforeHashtag = beforeUserHashtag.getHashtag();
      if (modifyHashtagList.contains(beforeHashtag)) {
        modifyHashtagList.remove(beforeHashtag);
      } else {
        //수정할 해시태그 리스트에 포함되어 있지 않으면 기존 유저해시태그 삭제
        beforeHashtag.setCount(beforeHashtag.getCount() - 1);
        this.userHashtagRepository.delete(beforeUserHashtag);
      }
    }

    //추가한 해시태그 저장
    for (Hashtag hashtag : modifyHashtagList) {
      UserHashtag userHashtag = new UserHashtag();

      hashtag.setCount(hashtag.getCount() + 1);

      userHashtag.setUser(user.get());
      userHashtag.setHashtag(hashtag);
      userHashtag.setRegistered(Timestamp.valueOf(LocalDateTime.now()));

      this.userHashtagRepository.save(userHashtag);
    }

    return this.userHashtagRepository.findByUserId(user.get().getId());
  }





  public List<UserHashtag> deleteUserHashtag(int userId, int[] hashtagId) {
    if (hashtagId != null) {
      for (int i = 0; i < hashtagId.length; i++) {

        //변수 정의
        UserHashtag userHashtag = this.userHashtagRepository.findByUserIdAndHashtagId(userId,
            hashtagId[i]);
        Optional<Hashtag> hashtag = this.hashtagRepository.findById(hashtagId[i]);

        //예외 처리
        if (this.userHashtagRepository.findByUserIdAndHashtagId(userId, hashtagId[i]) == null) {
          throw new ApiException(ApiExceptionEnum.USERHASHTAG_NOT_FOUND_EXCEPTION);
        }

        //데이터 처리
        hashtag.get().setCount(hashtag.get().getCount() - 1);


        //리턴
        this.userHashtagRepository.delete(userHashtag);

      }
      return this.userHashtagRepository.findByUserId(userId);
    }

    throw new ApiException(ApiExceptionEnum.BAD_REQUEST_EXCEPTION);
  }


}
