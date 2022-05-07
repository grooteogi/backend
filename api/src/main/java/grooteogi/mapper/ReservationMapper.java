package grooteogi.mapper;

import grooteogi.domain.Post;
import grooteogi.domain.Reservation;
import grooteogi.domain.Schedule;
import grooteogi.domain.User;
import grooteogi.dto.ReservationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ReservationMapper extends BasicMapper<ReservationDto, Reservation> {

  ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

  @Mapping(target = "id", ignore = true)
  @Mapping(source = "user", target = "participateUser")
  @Mapping(source = "schedule.post.user", target = "hostUser")
  Reservation toEntity(ReservationDto.Request dto, User user, Schedule schedule);

  ReservationDto.Response toResponseDto(Reservation reservation, Post post, Schedule schedule);
}
