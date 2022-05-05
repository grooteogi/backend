package grooteogi.service;

import grooteogi.domain.Hashtag;
import grooteogi.dto.hashtag.HashtagDto;
import grooteogi.enums.HashtagType;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.repository.HashtagRepository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HashtagService {

  private final HashtagRepository hashtagRepository;


  public Hashtag createHashtag(HashtagDto hashtagDto) {
    Hashtag createdHashtag = new Hashtag();

    if (this.hashtagRepository.findByTag(hashtagDto.getTag()) != null) {
      throw new ApiException(ApiExceptionEnum.DUPLICATION_VALUE_EXCEPTION);
    }

    //새로운 해시태그는 성격만 작성 가능
    createdHashtag.setHashtagType(HashtagType.PERSONALITY);
    createdHashtag.setRegistered(Timestamp.valueOf(LocalDateTime.now()));
    createdHashtag.setTag(hashtagDto.getTag());

    return this.hashtagRepository.save(createdHashtag);
  }

  public List<Hashtag> search(Pageable page, String type) {
    final List<Hashtag> hashtags;

    if (type == null) {
      hashtags = this.hashtagRepository.findAllByPage(page);
    } else if (!type.toLowerCase(Locale.ROOT).equals("personality")
        && !type.toLowerCase(Locale.ROOT).equals("concern")) {
      throw new ApiException(ApiExceptionEnum.BAD_REQUEST_EXCEPTION);
    } else {
      type = type.toLowerCase(Locale.ROOT);
      hashtags = this.hashtagRepository.findByTypeAndPage(type, page);
    }
    return hashtags;
  }
}
