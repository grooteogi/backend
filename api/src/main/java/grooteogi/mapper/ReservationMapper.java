package grooteogi.mapper;

import grooteogi.domain.Post;
import grooteogi.domain.Reservation;
import grooteogi.domain.Schedule;
import grooteogi.domain.User;
import grooteogi.dto.ReservationDto;
import grooteogi.dto.ReservationRes;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ReservationMapper extends BasicMapper<ReservationDto, Reservation> {
  ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);
  
  @Mapping(target = "id", ignore = true)
  @Mapping(source = "user", target = "participateUser")
  @Mapping(source = "schedule.post.user", target = "hostUser")
  Reservation toEntity(ReservationDto dto, User user, Schedule schedule);

  ReservationDto toDto(Reservation entity);

  ReservationRes toResDto(Reservation eReservation, Post ePost, Schedule eSchedule);

}
