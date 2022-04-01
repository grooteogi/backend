package grooteogi.service;


import grooteogi.domain.Hashtag;
import grooteogi.enums.HashtagType;
import grooteogi.repository.HashtagRepository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HashtagService {

  private final HashtagRepository hashtagRepository;

  public List<Hashtag> getAllHashtag() {
    return this.hashtagRepository.findAll();
  }

  public Hashtag createHashtag(String tag) {
    Hashtag createdHashtag = new Hashtag();

    //새로운 해시태그는 성격만 작성 가능
    createdHashtag.setHashtagType(HashtagType.PERSONALITY);
    createdHashtag.setRegistered(Timestamp.valueOf(LocalDateTime.now()));
    createdHashtag.setTag(tag);
    return this.hashtagRepository.save(createdHashtag);
  }

}
