package grooteogi.mapper;

import grooteogi.domain.Post;
import grooteogi.domain.Reservation;
import grooteogi.domain.Review;
import grooteogi.domain.User;
import grooteogi.domain.UserInfo;
import grooteogi.dto.ReviewDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper extends BasicMapper<ReviewDto, Review> {

  ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);

  @Mapping(source = "dto.text", target = "text")
  @Mapping(source = "dto.score", target = "score")
  @Mapping(source = "post", target = "post")
  @Mapping(source = "reservation", target = "reservation")
  @Mapping(source = "user", target = "user")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createAt", ignore = true)
  @Mapping(target = "updateAt", ignore = true)
  Review toEntity(ReviewDto.Request dto, Post post, Reservation reservation, User user);

  @Mapping(source = "dto.text", target = "text")
  @Mapping(source = "dto.score", target = "score")
  @Mapping(source = "review.id", target = "id")
  Review toModify(ReviewDto.Request dto, Review review);

  @Mapping(source = "review.id", target = "reviewId")
  @Mapping(source = "review.score", target = "score")
  @Mapping(source = "review.text", target = "text")
  @Mapping(source = "review.createAt", target = "createAt")
  @Mapping(source = "user.nickname", target = "nickname")
  @Mapping(source = "userInfo.imageUrl", target = "imageUrl")
  ReviewDto.Response toReviewResponse(Review review, User user,
      UserInfo userInfo);
}
