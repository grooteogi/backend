package grooteogi.service;

import grooteogi.domain.Post;
import grooteogi.domain.PostHashtag;
import grooteogi.domain.Reservation;
import grooteogi.domain.Schedule;
import grooteogi.domain.User;
import grooteogi.dto.ReservationDto;
import grooteogi.enums.ReservationType;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.mapper.ReservationMapper;
import grooteogi.repository.PostRepository;
import grooteogi.repository.ReservationRepository;
import grooteogi.repository.ScheduleRepository;
import grooteogi.repository.UserRepository;
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

  public ReservationDto.Responses getReservation(Integer reservationId) {
    Optional<Reservation> reservation = reservationRepository.findById(reservationId);
    if (reservation.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.RESERVATION_NOT_FOUND_EXCEPTION);
    }
    Schedule schedule = reservation.get().getSchedule();
    Post post = schedule.getPost();
    ReservationDto.Responses responses = ReservationMapper.INSTANCE
        .toResponseDtos(reservation.get(), post, schedule);
    return responses;
  }

  public List<ReservationDto.Responses> getHostReservation(int userId) {
    List<Reservation> reservations = reservationRepository.findByHostUserId(userId);
    return getReservationDto(reservations);
  }

  public List<ReservationDto.Responses> getUserReservation(Integer userId) {
    List<Reservation> reservations = reservationRepository.findByParticipateUserId(userId);
    return getReservationDto(reservations);
  }

  private List<ReservationDto.Responses> getReservationDto(List<Reservation> reservations) {
    if (reservations.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.RESERVATION_NOT_FOUND_EXCEPTION);
    }
    List<ReservationDto.Responses> responseList = new ArrayList<>();
    reservations.forEach(reservation -> {
      /*
      * 1. DB에 저장된 Reservation을 다 불러와
      * 2. reservation의 스케줄을 꺼낸다.
      * 3. 스케쥴에서 date를 꺼낸다.
      * 4. date가 현재 날짜를 기준으로 계산
      * 5. reservation의 상태를 [ 완료 ]로 바꾼다.
      * */
      Schedule schedule = reservation.getSchedule();
      Post post = schedule.getPost();

      ReservationDto.Responses responses =
          ReservationMapper.INSTANCE.toResponseDtos(reservation, post, schedule);
      responses.setHashtags(getTags(post.getPostHashtags()));
      responseList.add(responses);
    });
    return responseList;
  }

  private List<String> getTags(List<PostHashtag> postHashtags) {
    List<String> tags = new ArrayList<>();
    postHashtags.forEach(postHashtag -> tags.add(postHashtag.getHashTag().getTag()));
    return tags;
  }

  public ReservationDto.Response createReservation(ReservationDto.Request request, int userId) {

    Optional<Reservation> reservation = reservationRepository.findByScheduleId(
        request.getScheduleId());

    if (reservation.isPresent()) {
      throw new ApiException(ApiExceptionEnum.DUPLICATION_RESERVATION_EXCEPTION);
    }

    Optional<Schedule> schedule = scheduleRepository.findById(request.getScheduleId());

    if (schedule.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.SCHEDULE_NOT_FOUND_EXCEPTION);
    }

    Optional<User> user = userRepository.findById(userId);

    boolean isWriter = postRepository.existsByUser(user.get());
    if (isWriter) {
      throw new ApiException(ApiExceptionEnum.BAD_REQUEST_EXCEPTION);
    }

    Reservation createdReservation =
        reservationRepository.save(ReservationMapper.INSTANCE.toEntity(request, user.get(),
        schedule.get(), ReservationType.UNCANCELED));
    return ReservationMapper.INSTANCE.toResponseDto(createdReservation);
  }

  public void deleteReservation(Integer reservationId) {
    Optional<Reservation> reservation = reservationRepository.findById(reservationId);
    if (reservation.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.NOT_FOUND_EXCEPTION);
    }
    reservationRepository.delete(reservation.get());
  }
}
