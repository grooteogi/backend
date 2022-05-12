package grooteogi.mapper;

import grooteogi.domain.Schedule;
import grooteogi.dto.ScheduleDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = DateMapper.class,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ScheduleMapper extends BasicMapper<ScheduleDto, Schedule>  {

  ScheduleMapper INSTANCE = Mappers.getMapper(ScheduleMapper.class);

  @Mappings({
      @Mapping(target = "date", source = "dto.date", dateFormat = "yyyy-MM-dd"),
      @Mapping(target = "startTime", source = "dto.startTime", dateFormat = "HH:mm:ss"),
      @Mapping(target = "endTime", source = "dto.endTime", dateFormat = "HH:mm:ss")
  })
  Schedule toEntity(ScheduleDto.Request dto);

}
