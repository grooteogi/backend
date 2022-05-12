package grooteogi.mapper;

import grooteogi.domain.Post;
import grooteogi.domain.Reservation;
import grooteogi.domain.Schedule;
import grooteogi.domain.User;
import grooteogi.dto.ReservationDto;
import grooteogi.enums.ReservationType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = DateMapper.class,
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
}
