package grooteogi.service;

import grooteogi.domain.Hashtag;
import grooteogi.dto.HashtagDto;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.mapper.HashtagMapper;
import grooteogi.repository.HashtagRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HashtagService {

  private final HashtagRepository hashtagRepository;


  public HashtagDto.Response createHashtag(HashtagDto.Request request) {
    Optional<Hashtag> findHashtag = this.hashtagRepository.findByName(request.getName());

    if (!findHashtag.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.DUPLICATION_VALUE_EXCEPTION);
    }

    Hashtag createdHashtag = HashtagMapper.INSTANCE.toEntity(request.getName());
    Hashtag savedHashtag = hashtagRepository.save(createdHashtag);

    return HashtagMapper.INSTANCE.toResponseDto(savedHashtag);

  }

  public List<HashtagDto.Response> getHashtag() {

    List<Hashtag> hashtags = hashtagRepository.findByCount(10);

    List<HashtagDto.Response> responseList = new ArrayList<>();

    hashtags.forEach(hashtag -> responseList.add(HashtagMapper.INSTANCE.toResponseDto(hashtag)));

    return responseList;
  }

  public HashtagDto.Response search(String keyword) {

    Optional<Hashtag> hashtag = (keyword == null || keyword.equals("")) ? hashtagRepository.findAll(
        Sort.by(Sort.Direction.DESC, "count")).stream().findFirst()
        : hashtagRepository.findByName(keyword);

    if (hashtag.isEmpty()) {
      Hashtag createdHashtag = HashtagMapper.INSTANCE.toEntity(keyword);
      Hashtag savedHashtag = hashtagRepository.save(createdHashtag);
      return HashtagMapper.INSTANCE.toResponseDto(savedHashtag);
    }

    return HashtagMapper.INSTANCE.toResponseDto(hashtag.get());
  }

}
