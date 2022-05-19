package grooteogi.service;

import grooteogi.domain.Post;
import grooteogi.domain.Reservation;
import grooteogi.domain.Review;
import grooteogi.domain.User;
import grooteogi.dto.ReviewDto;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.mapper.ReviewMapper;
import grooteogi.repository.PostRepository;
import grooteogi.repository.ReservationRepository;
import grooteogi.repository.ReviewRepository;
import grooteogi.repository.ScheduleRepository;
import grooteogi.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final ReservationRepository reservationRepository;
  private final PostRepository postRepository;
  private final ScheduleRepository scheduleRepository;
  private final UserRepository userRepository;

  public void createReview(ReviewDto.Request request, Integer userId) {

    Optional<Reservation> reservation = reservationRepository.findByScheduleId(
        request.getReservationId());

    if (reservation.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.RESERVATION_NOT_FOUND_EXCEPTION);
    }

    Optional<Post> post = postRepository.findById(request.getPostId());

    if (post.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.POST_NOT_FOUND_EXCEPTION);
    }

    Optional<User> user = userRepository.findById(userId);

    boolean isWriter = postRepository.existsByUser(user.get());
    if (isWriter) {
      throw new ApiException(ApiExceptionEnum.REVIEW_HOST_EXCEPTION);
    }

    reviewRepository.save(ReviewMapper.INSTANCE.toEntity(request));
  }

  public void deleteReview(Integer reviewId) {
    Optional<Review> review = reviewRepository.findById(reviewId);
    if (review.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.REVIEW_NOT_FOUND_EXCEPTION);
    }
    reviewRepository.delete(review.get());
  }

  public void modifyReview(ReviewDto.Request request, Integer reviewId) {

    Optional<Review> review = reviewRepository.findById(reviewId);

    if (review.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.RESERVATION_NOT_FOUND_EXCEPTION);
    }

    review.get().setScore(request.getScore());
    review.get().setText(request.getText());

    reviewRepository.save(review.get());
  }
}
