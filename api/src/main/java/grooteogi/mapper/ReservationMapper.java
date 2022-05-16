package grooteogi.mapper;

import grooteogi.domain.Post;
import grooteogi.domain.Reservation;
import grooteogi.domain.Schedule;
import grooteogi.domain.User;
import grooteogi.dto.ReservationDto;
import grooteogi.enums.ReservationType;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReservationMapper extends BasicMapper<ReservationDto, Reservation> {

  ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

  @Mappings({
      @Mapping(source = "status", target = "status"),
      @Mapping(target = "id", ignore = true),
      @Mapping(source = "schedule", target = "schedule"),
      @Mapping(source = "user", target = "participateUser"),
      @Mapping(source = "schedule.post.user", target = "hostUser")
  })
  Reservation toEntity(ReservationDto.Request dto, User user,
      Schedule schedule, ReservationType status);

  @Mapping(target = "reservationId", source = "reservation.id")
  ReservationDto.Response toResponseDto(Reservation reservation);

  @Mappings({
      @Mapping(target = "date", dateFormat = "yyyy-MM-dd"),
      @Mapping(target = "startTime", dateFormat = "HH:mm:ss"),
      @Mapping(target = "endTime", dateFormat = "HH:mm:ss"),
      @Mapping(target = "postId", source = "post.id"),
      @Mapping(target = "status", source = "reservation.status")
  })
  ReservationDto.Responses toResponseDtos(Reservation reservation, Post post, Schedule schedule);

  default ReservationType map(Integer value) {
    return ReservationType.valueOf(value);
  }

  default Integer map(ReservationType status) {
    return status.getValue();
  }

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
}
