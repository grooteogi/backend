package grooteogi.service;

import grooteogi.domain.Post;
import grooteogi.domain.Reservation;
import grooteogi.domain.Review;
import grooteogi.domain.Schedule;
import grooteogi.domain.User;
import grooteogi.dto.ReviewDto;
import grooteogi.dto.ReviewDto.Request;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.mapper.ReviewMapper;
import grooteogi.repository.PostRepository;
import grooteogi.repository.ReservationRepository;
import grooteogi.repository.ReviewRepository;
import grooteogi.repository.ScheduleRepository;
import grooteogi.repository.UserRepository;
import java.sql.Date;
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

    Optional<Reservation> reservation = reservationRepository.findByIdUncanceled(
        request.getReservationId());

    if (reservation.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.RESERVATION_NOT_FOUND_EXCEPTION);
    }

    Optional<User> user = userRepository.findById(userId);

    Schedule schedule = reservation.get().getSchedule();
    Post post = schedule.getPost();

    boolean isWriter = user.get().getId() == post.getUser().getId();
    if (isWriter) {
      throw new ApiException(ApiExceptionEnum.REVIEW_HOST_EXCEPTION);
    }

    long miliseconds = System.currentTimeMillis();
    Date now = new Date(miliseconds);
    if (now.before(schedule.getDate())) {
      throw new ApiException(ApiExceptionEnum.NO_CREATE_REVIEW_EXCEPTION);
    }

    Review review = ReviewMapper.INSTANCE.toEntity(request, post, reservation.get(), user.get());

    reviewRepository.save(review);
  }

  public void deleteReview(Integer reviewId, Integer userId) {
    Optional<Review> review = reviewRepository.findById(reviewId);
    if (review.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.REVIEW_NOT_FOUND_EXCEPTION);
    }

    int writer = review.get().getUser().getId();
    if (writer != userId) {
      throw new ApiException(ApiExceptionEnum.NO_PERMISSION_EXCEPTION);
    }

    reviewRepository.delete(review.get());
  }

  public void modifyReview(Request request, Integer reviewId, Integer userId) {

    Optional<Review> review = reviewRepository.findById(reviewId);

    if (review.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.REVIEW_NOT_FOUND_EXCEPTION);
    }

    int writer = review.get().getUser().getId();
    if (writer != userId) {
      throw new ApiException(ApiExceptionEnum.NO_PERMISSION_EXCEPTION);
    }

    Review modified = ReviewMapper.INSTANCE.toModify(request, review.get());

    reviewRepository.save(modified);
  }
}
