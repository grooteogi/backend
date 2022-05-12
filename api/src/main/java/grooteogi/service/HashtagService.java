package grooteogi.service;

import grooteogi.domain.Hashtag;
import grooteogi.dto.hashtag.HashtagDto;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.mapper.HashtagMapper;
import grooteogi.repository.HashtagRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HashtagService {

  private final HashtagRepository hashtagRepository;


  public HashtagDto.Response createHashtag(HashtagDto.Request request) {
    Hashtag createdHashtag = new Hashtag();

    if (this.hashtagRepository.findByName(request.getName()) != null) {
      throw new ApiException(ApiExceptionEnum.DUPLICATION_VALUE_EXCEPTION);
    }

    //새로운 해시태그는 성격만 작성 가능
    createdHashtag = hashtagRepository.save(HashtagMapper.INSTANCE.toEntity(request));


    return HashtagMapper.INSTANCE.toResponseDto(createdHashtag);

  }

  public List<HashtagDto.Response> getHashtag() {

    List<Hashtag> hashtags = hashtagRepository.findByCount(10);

    List<HashtagDto.Response> responseList = new ArrayList<>();

    hashtags.forEach(hashtag -> {
      responseList.add(HashtagMapper.INSTANCE.toResponseDto(hashtag));
    });

    return responseList;
  }

  public List<Hashtag> search(String keyword) {
    Optional<Hashtag> findHashtag = this.hashtagRepository.findByName(keyword);


    if (findHashtag.isEmpty()) {
      Hashtag createdHashtag = Hashtag.builder().name(keyword).build();
      hashtagRepository.save(createdHashtag);
    }

    List<Hashtag> hashtags = this.hashtagRepository.findAllByName(keyword);

    return hashtags;
  }


}
