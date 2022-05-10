package grooteogi.mapper;

import grooteogi.domain.Post;
import grooteogi.domain.Reservation;
import grooteogi.domain.Schedule;
import grooteogi.domain.User;
import grooteogi.dto.ReservationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ReservationMapper extends BasicMapper<ReservationDto, Reservation> {

  ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

 @Mappings({
     @Mapping(target = "id", ignore = true),
     @Mapping(source = "schedule", target = "schedule"),
     @Mapping(source = "user", target = "participateUser"),
     @Mapping(source = "schedule.post.user", target = "hostUser")
 })
  Reservation toEntity(ReservationDto.Request dto, User user, Schedule schedule);

@Mappings({
    @Mapping(target = "date", dateFormat = "yyyy-MM-dd"),
    @Mapping(target = "startTime", dateFormat = "HH:mm:ss"),
    @Mapping(target = "endTime", dateFormat = "HH:mm:ss"),
    @Mapping(source = "post.imageUrl", target = "imageUrl")
})
  ReservationDto.Response toResponseDto(Reservation reservation, Post post, Schedule schedule);
}
