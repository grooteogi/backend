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
import java.sql.Date;
import java.text.SimpleDateFormat;
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

  public List<ReservationDto.Responses> getHostReservation(int userId, String sort) {
    // CANCEL 여부 확인
    ReservationType status =
        (sort.equals("CANCEL")) ? ReservationType.CANCELED : ReservationType.UNCANCELED;
    List<Reservation> reservations =
        reservationRepository.findByHostUserIdAndStatusOrderByIdDesc(userId, status);

    // DATE 필터링
    List<ReservationDto.Responses> responses = (status.getValue() == 0)
        ? getReservationDto(reservations) : getSortedReservationDto(reservations, sort);
    return responses;
  }

  public List<ReservationDto.Responses> getUserReservation(Integer userId, String sort) {
    // CANCEL 여부 확인
    ReservationType status =
        (sort.equals("CANCEL")) ? ReservationType.CANCELED : ReservationType.UNCANCELED;
    List<Reservation> reservations =
        reservationRepository.findByParticipateUserIdAndStatusOrderByIdDesc(userId, status);
    
    // DATE 필터링
    List<ReservationDto.Responses> responses = (status.getValue() == 0)
        ? getReservationDto(reservations) : getSortedReservationDto(reservations, sort);
    return responses;
  }

  private List<ReservationDto.Responses> getSortedReservationDto(
      List<Reservation> reservations, String sort) {

    if (reservations.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.RESERVATION_NOT_FOUND_EXCEPTION);
    }
    List<ReservationDto.Responses> responseList = new ArrayList<>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String ss= sdf.format(new java.util.Date());
    Date now = Date.valueOf(ss);
    reservations.forEach(reservation -> {
      Schedule schedule = reservation.getSchedule();
      Post post = schedule.getPost();
      Date date = schedule.getDate();
      if (sort.equals("PROCEED")) {
        if (date.before(now)) {
          ReservationDto.Responses responses =
              ReservationMapper.INSTANCE.toResponseDtos(reservation, post, schedule);
          responses.setHashtags(getTags(post.getPostHashtags()));
          responseList.add(responses);
        }
      } else if (sort.equals("COMPLETE")) {
        if (date.after(now)) {
          ReservationDto.Responses responses =
              ReservationMapper.INSTANCE.toResponseDtos(reservation, post, schedule);
          responses.setHashtags(getTags(post.getPostHashtags()));
          responseList.add(responses);
        }
      } else {
        ReservationDto.Responses responses =
            ReservationMapper.INSTANCE.toResponseDtos(reservation, post, schedule);
        responses.setHashtags(getTags(post.getPostHashtags()));
        responseList.add(responses);
      }

    });
    return responseList;
  }

  private List<ReservationDto.Responses> getReservationDto(List<Reservation> reservations) {
    if (reservations.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.RESERVATION_NOT_FOUND_EXCEPTION);
    }
    List<ReservationDto.Responses> responseList = new ArrayList<>();
    reservations.forEach(reservation -> {

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

  public ReservationDto.Response modifyStatus(Integer reservationId) {

    Optional<Reservation> reservation = reservationRepository.findById(reservationId);

    if (reservation.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.RESERVATION_NOT_FOUND_EXCEPTION);
    }
    reservation.get().setStatus(ReservationType.CANCELED);
    reservationRepository.save(reservation.get());
    return ReservationMapper.INSTANCE.toResponseDto(reservation.get());
  }
}
