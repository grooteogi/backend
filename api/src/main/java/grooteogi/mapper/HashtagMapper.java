package grooteogi.mapper;

import grooteogi.domain.Hashtag;
import grooteogi.dto.hashtag.HashtagDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HashtagMapper extends BasicMapper<HashtagDto, Hashtag> {

  HashtagMapper INSTANCE = Mappers.getMapper(HashtagMapper.class);

  @Mapping(source = "dto.name", target = "name")
  Hashtag toEntity(HashtagDto.Request dto);

  @Mappings({
      @Mapping(source = "hashtag.id", target = "hashtagId"),
      @Mapping(source = "hashtag.name", target = "name")
  })
  HashtagDto.Response toResponseDto(Hashtag hashtag);

}
