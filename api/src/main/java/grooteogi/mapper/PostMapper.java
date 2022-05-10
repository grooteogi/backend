package grooteogi.mapper;

import grooteogi.domain.Post;
import grooteogi.domain.PostHashtag;
import grooteogi.domain.Schedule;
import grooteogi.domain.User;
import grooteogi.dto.PostDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = DateMapper.class)
public interface PostMapper extends BasicMapper<PostDto, Post> {

  PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

  Post toEntity(PostDto.Request dto, User user,
      List<PostHashtag> postHashtags, List<Schedule> schedules);

  PostDto.Response toResponseDto(Post post);

  @Mappings({
      @Mapping(source = "dto.title", target = "title"),
      @Mapping(source = "dto.content", target = "content"),
      @Mapping(source = "dto.imageUrl", target = "imageUrl"),
      @Mapping(source = "dto.credit", target = "credit"),
      @Mapping(source = "post.schedules", target = "schedules"),
      @Mapping(source = "post.postHashtags", target = "postHashtags")
  })
  Post toModify(Post post, PostDto.Request dto);

}
