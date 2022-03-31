package grooteogi.service;

import grooteogi.domain.Hashtag;
import grooteogi.domain.HashtagType;
import grooteogi.repository.HashtagRepository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HashtagService {
  private final HashtagRepository hashtagRepository;

  @Autowired
  public HashtagService(HashtagRepository hashtagRepository) {
    this.hashtagRepository = hashtagRepository;
  }

  public List<Hashtag> getAllHashtag() {
    return this.hashtagRepository.findAll();
  }

  public List<Hashtag> getTopByCountHashtag() {
    //사용하는 사람이 많아졌을 때 사용하면 좋을 듯
    //현재는 getAllHashtag로 받아올 것!
    return this.hashtagRepository.getTopByCountHashtag();
  }

  public Hashtag createHashtag(String tag) {
    Hashtag created = new Hashtag();

    //새로운 해시태그는 성격만 작성 가능
    created.setHashtagType(HashtagType.PERSONALITY);
    created.setRegistered(Timestamp.valueOf(LocalDateTime.now()));
    created.setTag(tag);
    return this.hashtagRepository.save(created);
  }

}
