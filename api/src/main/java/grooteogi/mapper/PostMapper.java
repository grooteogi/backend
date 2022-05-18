package grooteogi.mapper;

import grooteogi.domain.Post;
import grooteogi.domain.Review;
import grooteogi.domain.Schedule;
import grooteogi.domain.User;
import grooteogi.domain.UserInfo;
import grooteogi.dto.PostDto;
import grooteogi.dto.PostDto.Request;
import grooteogi.dto.ScheduleDto;
import grooteogi.dto.UserDto;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper extends BasicMapper<PostDto, Post> {

  PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

  @Mapping(source = "dto.title", target = "title")
  @Mapping(source = "dto.content", target = "content")
  @Mapping(source = "dto.credit", target = "credit")
  @Mapping(source = "dto.imageUrl", target = "imageUrl")
  @Mapping(source = "user", target = "user")
  @Mapping(target = "id", ignore = true)
  Post toEntity(Request dto, User user);

  @Mapping(source = "post.id", target = "postId")
  @Mapping(source = "post.title", target = "title")
  @Mapping(source = "post.content", target = "content")
  @Mapping(source = "post.imageUrl", target = "imageUrl")
  @Mapping(source = "post.createAt", target = "createAt")
  @Mapping(source = "post.credit", target = "creditType")
  @Mapping(source = "post.hearts", target = "likes", ignore = true)
  @Mapping(source = "post.user", target = "mentor", ignore = true)
  PostDto.Response toDetailResponse(Post post);

  @Mapping(source = "review.id", target = "reviewId")
  @Mapping(source = "review.score", target = "score")
  @Mapping(source = "review.text", target = "text")
  @Mapping(source = "review.createAt", target = "createAt")
  @Mapping(source = "user.nickname", target = "nickname")
  @Mapping(source = "userInfo.imageUrl", target = "imageUrl")
  PostDto.ReviewResponse toReviewResponse(Review review, User user,
      UserInfo userInfo);

  @Mapping(source = "post.id", target = "postId")
  PostDto.CreateResponse toCreateResponseDto(Post post);

  @Mapping(source = "post.id", target = "postId")
  @Mapping(source = "post.title", target = "title")
  @Mapping(source = "post.content", target = "content")
  @Mapping(source = "post.imageUrl", target = "imageUrl")
  @Mapping(source = "post.postHashtags", target = "hashtags", ignore = true)
  PostDto.SearchResponse toSearchResponseDto(Post post);

  @Mappings({
      @Mapping(target = "date", source = "dto.date", dateFormat = "yyyy-MM-dd"),
      @Mapping(target = "startTime", source = "dto.startTime", dateFormat = "HH:mm:ss"),
      @Mapping(target = "endTime", source = "dto.endTime", dateFormat = "HH:mm:ss")
  })
  Schedule toScheduleEntity(ScheduleDto.Request dto);

  @Mappings({
      @Mapping(target = "date", source = "dto.date", dateFormat = "yyyy-MM-dd"),
      @Mapping(target = "startTime", source = "dto.startTime", dateFormat = "HH:mm:ss"),
      @Mapping(target = "endTime", source = "dto.endTime", dateFormat = "HH:mm:ss")
  })
  List<Schedule> toScheduleEntities(List<ScheduleDto.Request> dto);


  @Mapping(source = "schedules.id", target = "scheduleId")
  @Mapping(source = "schedules.date", target = "date", dateFormat = "yyyy-MM-dd")
  @Mapping(source = "schedules.startTime", target = "startTime", dateFormat = "HH:mm:ss")
  @Mapping(source = "schedules.endTime", target = "endTime", dateFormat = "HH:mm:ss")
  @Mapping(source = "schedules.region", target = "region")
  @Mapping(source = "schedules.place", target = "place")
  ScheduleDto.Response toScheduleResponses(Schedule schedules);


  default String asStringDate(Date date) {
    return date != null ? new SimpleDateFormat("yyyy-MM-dd")
        .format(date) : null;
  }

  default Date asDate(String date) {
    if (date == null) {
      return null;
    } else {
      return java.sql.Date.valueOf(date);
    }
  }

  default String asStringTime(Time time) {
    return time != null ? new SimpleDateFormat("HH:mm:ss")
        .format(time) : null;
  }

  default Time adTime(String time) {
    if (time == null) {
      return null;
    } else {
      return java.sql.Time.valueOf(time);
    }
  }

  @Mapping(source = "dto.title", target = "title")
  @Mapping(source = "dto.content", target = "content")
  @Mapping(source = "dto.credit", target = "credit")
  @Mapping(source = "dto.imageUrl", target = "imageUrl")
  @Mapping(source = "post.schedules", target = "schedules", ignore = true)
  Post toModify(Post post, PostDto.Request dto);

  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "user.nickname", target = "nickname")
  @Mapping(source = "userInfo.imageUrl", target = "imageUrl")
  UserDto.Response toUserResponse(User user, UserInfo userInfo);
}
