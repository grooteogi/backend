package grooteogi.mapper;

import grooteogi.domain.Post;
import grooteogi.domain.PostHashtag;
import grooteogi.domain.User;
import grooteogi.dto.PostDto;
import grooteogi.dto.ScheduleDto;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper( componentModel = "spring",
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface PostMapper extends BasicMapper<PostDto, Post> {

  PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

  Post toEntity(PostDto.Request dto, User user,
      List<PostHashtag> postHashtags, List<ScheduleDto.Request> schedules);

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

  default String asStringDate(Date date) {
    return date != null ? new SimpleDateFormat("yyyy-MM-dd")
        .format(date) : null;
  }

  default Date asDate(String date) {
    if (date == null) {
      return null;
    } else {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      String ss = sdf.format(new java.util.Date());
      return Date.valueOf(ss);
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
      SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
      String ss = sdf.format(new java.util.Date());
      return Time.valueOf(ss);
    }
  }

//  default Schedule toEntity(ScheduleDto.Request request) {
//    if (request == null) {
//      return null;
//    }
//
//    ScheduleBuilder schedule = Schedule.builder();
//
//    schedule.date(asDate(request.getDate()));
//    schedule.startTime(adTime(request.getStartTime()));
//    schedule.endTime(adTime(request.getEndTime()));
//    schedule.region(request.getRegion());
//    schedule.place(request.getPlace());
//
//    return schedule.build();
//  }
}
