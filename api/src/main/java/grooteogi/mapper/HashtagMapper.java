package grooteogi.mapper;

import grooteogi.domain.Hashtag;
import grooteogi.domain.PostHashtag;
import grooteogi.dto.HashtagDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HashtagMapper extends BasicMapper<HashtagDto, Hashtag> {

  HashtagMapper INSTANCE = Mappers.getMapper(HashtagMapper.class);

  @Mapping(source = "name", target = "name")
  @Mapping(target = "id", ignore = true)
  Hashtag toEntity(String name);

  @Mappings({
      @Mapping(source = "hashtag.id", target = "hashtagId"),
      @Mapping(source = "hashtag.name", target = "name")
  })
  HashtagDto.Response toResponseDto(Hashtag hashtag);

  @Mappings({
      @Mapping(source = "postHashtag.id", target = "hashtagId"),
      @Mapping(source = "hashtag.name", target = "name")
  })
  HashtagDto.Response toPostResponseDto(PostHashtag postHashtag, Hashtag hashtag);
}
