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
    //count가 0이어도 불러올 수 있게 함
    //타입별 getTopTenHashtag로 불러오기
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
