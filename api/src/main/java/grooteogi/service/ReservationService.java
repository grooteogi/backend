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

  public List<ReservationRes> getPostReservation(int id) {
    List<Object[]> reservations = reservationRepository.findPostReservation(id);

    if (reservations.size() == 0) {
      throw new ApiException(ApiExceptionEnum.RESERVATION_NOT_FOUND_EXCEPTION);
    }

    List<ReservationRes> responseList = new ArrayList<>();
    for (Object[] arr : reservations) {
      Reservation reservation = (Reservation) arr[0];
      Schedule schedule = (Schedule) arr[1];
      Post post = (Post) arr[2];
      ReservationRes response = new ReservationRes();
      response.setId(reservation.getId());
      response.setDate(schedule.getDate());
      response.setEndTime(schedule.getEndTime());
      response.setPlace(schedule.getPlace());
      response.setStartTime(schedule.getStartTime());
      response.setTitle(post.getTitle());
      response.setHashtags(getTags(post.getPostHashtags()));
      response.setImgUrl(post.getImageUrl());
      responseList.add(response);
    }
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

  public List<Reservation> getUserReservation(Integer userId) {
    List<Reservation> reservations = reservationRepository.findByUserId(userId);
    if (reservations.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.RESERVATION_NOT_FOUND_EXCEPTION);
    }
    return reservations;
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

    Reservation createdReservation = new Reservation();
    createdReservation.setSchedule(schedule.get());
    createdReservation.setUser(user.get());
    createdReservation.setMessage(reservationDto.getMessage());
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
