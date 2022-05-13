package grooteogi.mapper;

import grooteogi.domain.Post;
import grooteogi.domain.Schedule;
import grooteogi.dto.PostDto;
import grooteogi.dto.ScheduleDto;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SourceTargetMapper {

  SourceTargetMapper MAPPER = Mappers.getMapper(SourceTargetMapper.class);

  Post toEntity(PostDto.Request s, @Context JpaContext ctx);

  @Mappings({
      @Mapping(target = "date", source = "dto.date", dateFormat = "yyyy-MM-dd"),
      @Mapping(target = "startTime", source = "dto.startTime", dateFormat = "HH:mm:ss"),
      @Mapping(target = "endTime", source = "dto.endTime", dateFormat = "HH:mm:ss"),
      @Mapping(target = "post", ignore = true)
  })
  Schedule toEntity(ScheduleDto.Request dto, @Context JpaContext ctx);

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
}