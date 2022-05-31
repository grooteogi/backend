package grooteogi.mapper;

import grooteogi.domain.Post;
import grooteogi.domain.Reservation;
import grooteogi.domain.Review;
import grooteogi.domain.Schedule;
import grooteogi.domain.User;
import grooteogi.dto.ReservationDto;
import grooteogi.dto.ReservationDto.Request;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReservationMapper extends BasicMapper<ReservationDto, Reservation> {

  ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

  @Mappings({
      @Mapping(target = "id", ignore = true),
      @Mapping(source = "schedule", target = "schedule"),
      @Mapping(source = "user", target = "participateUser"),
      @Mapping(source = "schedule.post.user", target = "hostUser"),
      @Mapping(source = "isCanceled", target = "isCanceled")
  })
  Reservation toEntity(Request dto, User user,
      Schedule schedule, boolean isCanceled);

  @Mapping(target = "reservationId", source = "reservation.id")
  ReservationDto.Response toResponseDto(Reservation reservation);

  @Mappings({
      @Mapping(target = "reservationId", source = "reservation.id"),
      @Mapping(target = "date", dateFormat = "yyyy-MM-dd"),
      @Mapping(target = "startTime", dateFormat = "HH:mm:ss"),
      @Mapping(target = "endTime", dateFormat = "HH:mm:ss"),
      @Mapping(target = "postId", source = "post.id"),
      @Mapping(target = "hostPhone", source = "hostPhone"),
      @Mapping(target = "applyPhone", source = "applyPhone"),
      @Mapping(target = "isCanceled", source = "reservation.isCanceled"),
      @Mapping(target = "applyNickname", source = "applyNickname"),
      @Mapping(target = "text", source = "reservation.message"),
      @Mapping(target = "review", source = "review.text", defaultValue = ""),
      @Mapping(target = "score", source = "review.score", defaultValue = "0L")
  })
  ReservationDto.DetailResponse toDetailResponseDto(Reservation reservation,
      Post post, Schedule schedule, Review review,
      String hostPhone, String applyPhone, String applyNickname);

  @Mapping(source = "isCanceled", target = "isCanceled")
  Reservation toModifyIsCanceled(Reservation reservation, boolean isCanceled);

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
