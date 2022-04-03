package grooteogi.service;


import grooteogi.domain.Hashtag;
import grooteogi.dto.HashtagDto;
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

  public List<Hashtag> getTopTenHashtag(String type) {
    //사용하는 사람이 많아졌을 때 사용하면 좋을 듯
    //현재는 getAllHashtag로 받아올 것!
    return this.hashtagRepository.getTopTenHashtag(type);
  }


  public Hashtag createHashtag(HashtagDto hashtagDto) {
    Hashtag createdHashtag = new Hashtag();

    //새로운 해시태그는 성격만 작성 가능
    createdHashtag.setHashtagType(HashtagType.PERSONALITY);
    createdHashtag.setRegistered(Timestamp.valueOf(LocalDateTime.now()));
    createdHashtag.setTag(hashtagDto.getTag());
    return this.hashtagRepository.save(createdHashtag);
  }

}
