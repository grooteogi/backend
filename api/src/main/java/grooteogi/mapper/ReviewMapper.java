package grooteogi.mapper;

import grooteogi.domain.Review;
import grooteogi.dto.ReviewDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper extends BasicMapper<ReviewDto, Review> {

  ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);

  Review toEntity(ReviewDto.Request dto);
}
