package grooteogi.service;

import grooteogi.domain.Post;
import grooteogi.domain.PostHashtag;
import grooteogi.domain.Reservation;
import grooteogi.domain.Schedule;
import grooteogi.domain.User;
import grooteogi.dto.ReservationDto;
import grooteogi.dto.ReservationRes;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.mapper.ReservationMapper;
import grooteogi.repository.PostRepository;
import grooteogi.repository.ReservationRepository;
import grooteogi.repository.ScheduleRepository;
import grooteogi.repository.UserRepository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {
  private final ReservationRepository reservationRepository;
  private final ScheduleRepository scheduleRepository;
  private final UserRepository userRepository;
  private final PostRepository postRepository;

  public List<ReservationRes> getHostReservation(int userId) {
    List<Reservation> reservations = reservationRepository.findByHostUserId(userId);
    return getReservationResponse(reservations);
  }

  public List<ReservationRes> getUserReservation(Integer userId) {
    List<Reservation> reservations = reservationRepository.findByParticipateUserId(userId);
    return getReservationResponse(reservations);
  }

  private List<ReservationRes> getReservationResponse(List<Reservation> reservations) {
    if (reservations.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.RESERVATION_NOT_FOUND_EXCEPTION);
    }

    List<ReservationRes> responseList = new ArrayList<>();
    reservations.forEach(reservation -> {
      Optional<Schedule> schedule = scheduleRepository.findById(reservation.getSchedule().getId());
      Optional<Post> post = postRepository.findById(schedule.get().getPost().getId());
      ReservationRes response = ReservationMapper.INSTANCE.toResDto(reservation, post.get(),
          schedule.get());
      response.setHashtags(getTags(post.get().getPostHashtags()));
      responseList.add(response);
    });
    return responseList;
  }

  private String[] getTags(List<PostHashtag> postHashtags) {
    String[] tags = new String[postHashtags.size()];
    postHashtags.forEach(tag -> {
      int i = 0;
      tags[i++] = tag.getHashTag().getTag();
    });
    return tags;
  }

  public Reservation getReservation(Integer reservationId) {
    Optional<Reservation> reservation = reservationRepository.findById(reservationId);
    if (reservation.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.RESERVATION_NOT_FOUND_EXCEPTION);
    }
    return reservation.get();
  }

  public Reservation createReservation(ReservationDto reservationDto, int userId) {
    Optional<Reservation> reservation = reservationRepository
        .findByScheduleId(reservationDto.getScheduleId());
    if (reservation.isPresent()) {
      throw new ApiException(ApiExceptionEnum.DUPLICATION_RESERVATION_EXCEPTION);
    }

    Optional<Schedule> schedule = scheduleRepository.findById(reservationDto.getScheduleId());
    if (schedule.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.SCHEDULE_NOT_FOUND_EXCEPTION);
    }

    Optional<User> user = userRepository.findById(userId);
    boolean isWriter = postRepository.existsByUser(user.get());
    if (isWriter) {
      throw new ApiException(ApiExceptionEnum.BAD_REQUEST_EXCEPTION);
    }

    Reservation createdReservation = ReservationMapper.INSTANCE
        .toEntity(reservationDto, user.get(), schedule.get());
    createdReservation.setCreateDate(Timestamp.valueOf(LocalDateTime.now()));
    return reservationRepository.save(createdReservation);
  }

  public void deleteReservation(Integer reservationId) {
    Optional<Reservation> reservation = reservationRepository.findById(reservationId);
    if (reservation.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.NOT_FOUND_EXCEPTION);
    }
    reservationRepository.delete(reservation.get());
  }
}
