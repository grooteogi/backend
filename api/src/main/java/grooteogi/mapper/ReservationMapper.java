package grooteogi.mapper;

import grooteogi.domain.Post;
import grooteogi.domain.Reservation;
import grooteogi.domain.Schedule;
import grooteogi.domain.User;
import grooteogi.dto.ReservationDto;
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
      @Mapping(target = "type", ignore = true)
  })
  Reservation toEntity(ReservationDto.Request dto, User user, Schedule schedule);

  @Mapping(target = "reservationId", source = "reservation.id")
  ReservationDto.Response toResponseDto(Reservation reservation);

  @Mappings({
      @Mapping(target = "date", dateFormat = "yyyy-MM-dd"),
      @Mapping(target = "startTime", dateFormat = "HH:mm:ss"),
      @Mapping(target = "endTime", dateFormat = "HH:mm:ss"),
      @Mapping(target = "postId", source = "post.id"),
      @Mapping(target = "hashtags", source = "post.postHashtags"),
      @Mapping(target = "status", source = "reservation.status")
  })
  ReservationDto.Responses toResponseDtos(Reservation reservation, Post post, Schedule schedule);
}
