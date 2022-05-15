package grooteogi.service;

import grooteogi.domain.Post;
import grooteogi.domain.PostHashtag;
import grooteogi.domain.Reservation;
import grooteogi.domain.Schedule;
import grooteogi.domain.User;
import grooteogi.dto.ReservationDto;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.mapper.ReservationMapper;
import grooteogi.repository.PostRepository;
import grooteogi.repository.ReservationRepository;
import grooteogi.repository.ScheduleRepository;
import grooteogi.repository.UserRepository;
import grooteogi.utils.SmsClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

  private final ReservationRepository reservationRepository;
  private final ScheduleRepository scheduleRepository;
  private final UserRepository userRepository;
  private final PostRepository postRepository;

  private final SmsClient smsClient;

  public ReservationDto.Response getReservation(Integer reservationId) {
    Optional<Reservation> reservation = reservationRepository.findById(reservationId);
    if (reservation.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.RESERVATION_NOT_FOUND_EXCEPTION);
    }
    Schedule schedule = reservation.get().getSchedule();
    Post post = schedule.getPost();
    ReservationDto.Response response = ReservationMapper.INSTANCE
        .toResponseDto(reservation.get(), post, schedule);
    return response;
  }

  public List<ReservationDto.Response> getHostReservation(int userId) {
    List<Reservation> reservations = reservationRepository.findByHostUserId(userId);
    return getReservationDto(reservations);
  }

  public List<ReservationDto.Response> getUserReservation(Integer userId) {
    List<Reservation> reservations = reservationRepository.findByParticipateUserId(userId);
    return getReservationDto(reservations);
  }

  private List<ReservationDto.Response> getReservationDto(List<Reservation> reservations) {
    if (reservations.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.RESERVATION_NOT_FOUND_EXCEPTION);
    }
    List<ReservationDto.Response> responseList = new ArrayList<>();
    reservations.forEach(reservation -> {

      Schedule schedule = reservation.getSchedule();
      Post post = schedule.getPost();

      ReservationDto.Response response = ReservationMapper.INSTANCE.toResponseDto(reservation, post,
          schedule);
      response.setHashtags(getTags(post.getPostHashtags()));
      responseList.add(response);
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
        schedule.get()));
    Post post = schedule.get().getPost();
    return ReservationMapper.INSTANCE.toResponseDto(createdReservation, post, schedule.get());
  }

  public void deleteReservation(Integer reservationId) {
    Optional<Reservation> reservation = reservationRepository.findById(reservationId);
    if (reservation.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.NOT_FOUND_EXCEPTION);
    }
    reservationRepository.delete(reservation.get());
  }

  public ReservationDto.SmsCode sendSms(String phoneNumber) {

    Random rand = new Random();
    StringBuilder numStr = new StringBuilder();
    for (int i = 0; i < 4; i++) {
      String ran = Integer.toString(rand.nextInt(10));
      numStr.append(ran);
    }

    smsClient.certifiedPhoneNumber(phoneNumber, numStr.toString());
    return ReservationDto.SmsCode.builder()
        .code(numStr.toString()).build();
  }
}
