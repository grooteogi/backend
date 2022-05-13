package grooteogi.mapper;

import grooteogi.domain.Schedule;
import grooteogi.dto.ScheduleDto;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ScheduleMapper extends BasicMapper<ScheduleDto, Schedule>  {

  ScheduleMapper INSTANCE = Mappers.getMapper(ScheduleMapper.class);




  @Mappings({
      @Mapping(target = "date", source = "dto.date", dateFormat = "yyyy-MM-dd"),
      @Mapping(target = "startTime", source = "dto.startTime", dateFormat = "HH:mm:ss"),
      @Mapping(target = "endTime", source = "dto.endTime", dateFormat = "HH:mm:ss")
  })
  List<Schedule> toEntities(List<ScheduleDto.Request> dto);


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
