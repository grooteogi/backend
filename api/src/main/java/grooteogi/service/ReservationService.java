package grooteogi.service;

import grooteogi.domain.Post;
import grooteogi.domain.PostHashtag;
import grooteogi.domain.Reservation;
import grooteogi.domain.Schedule;
import grooteogi.domain.User;
import grooteogi.dto.ReservationDto;
import grooteogi.dto.ReservationDto.CheckSmsRequest;
import grooteogi.dto.ReservationDto.SendSmsResponse;
import grooteogi.enums.ReservationType;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.mapper.ReservationMapper;
import grooteogi.repository.PostRepository;
import grooteogi.repository.ReservationRepository;
import grooteogi.repository.ScheduleRepository;
import grooteogi.repository.UserRepository;
import grooteogi.utils.RedisClient;
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
  private final RedisClient redisClient;

  private final String prefix = "sms_verify";

  private final SmsClient smsClient;

  public ReservationDto.Responses getReservation(Integer reservationId) {
    Optional<Reservation> reservation = reservationRepository.findById(reservationId);
    if (reservation.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.RESERVATION_NOT_FOUND_EXCEPTION);
    }
    Schedule schedule = reservation.get().getSchedule();
    Post post = schedule.getPost();
    return ReservationMapper.INSTANCE.toResponseDtos(reservation.get(), post, schedule);
  }

  public List<ReservationDto.Responses> getHostReservation(int userId, String sort) {

    List<Reservation> result = new ArrayList<>();
    switch (sort) {
      case "ALL":
        result = reservationRepository.findByHost(userId);
        break;
      case "PROCEED":
        result = reservationRepository.findByHostProceed(userId);
        break;
      case "COMPLETE":
        result = reservationRepository.findByHostComplete(userId);
        break;
      case "CANCEL":
        result = reservationRepository.findByHostReservation(userId);
        break;
      default:
        break;
    }

    return getReservationDto(result);
  }

  public List<ReservationDto.Responses> getUserReservation(Integer userId, String sort) {

    List<Reservation> result = new ArrayList<>();
    switch (sort) {
      case "ALL":
        result = reservationRepository.findByPart(userId);
        break;
      case "PROCEED":
        result = reservationRepository.findByPartProceed(userId);
        break;
      case "COMPLETE":
        result = reservationRepository.findByPartComplete(userId);
        break;
      case "CANCEL":
        result = reservationRepository.findByPartReservation(userId);
        break;
      default:
        break;
    }

    return getReservationDto(result);
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
    postHashtags.forEach(postHashtag -> tags.add(postHashtag.getHashTag().getName()));
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
      throw new ApiException(ApiExceptionEnum.RESERVATION_HOST_EXCEPTION);
    }

    Reservation createdReservation =
        reservationRepository.save(ReservationMapper.INSTANCE.toEntity(request, user.get(),
            schedule.get(), ReservationType.UNCANCELED));
    return ReservationMapper.INSTANCE.toResponseDto(createdReservation);
  }

  public void deleteReservation(Integer reservationId, Integer userId) {
    Optional<Reservation> reservation = reservationRepository.findById(reservationId);
    if (reservation.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.RESERVATION_NOT_FOUND_EXCEPTION);
    }
    int hostId = reservation.get().getHostUser().getId();
    int participateId = reservation.get().getParticipateUser().getId();

    if (hostId != userId && participateId != userId) {
      throw new ApiException(ApiExceptionEnum.NO_PERMISSION_EXCEPTION);
    }

    reservationRepository.delete(reservation.get());
  }

  public ReservationDto.Response modifyStatus(Integer reservationId, Integer userId) {

    Optional<Reservation> reservation = reservationRepository.findByUncanceld(reservationId);

    if (reservation.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.RESERVATION_NOT_FOUND_EXCEPTION);
    }

    int hostId = reservation.get().getHostUser().getId();
    int participateId = reservation.get().getParticipateUser().getId();

    if (hostId != userId && participateId != userId) {
      throw new ApiException(ApiExceptionEnum.NO_PERMISSION_EXCEPTION);
    }

    reservation.get().setStatus(ReservationType.CANCELED);
    reservationRepository.save(reservation.get());
    return ReservationMapper.INSTANCE.toResponseDto(reservation.get());
  }

  public SendSmsResponse sendSms(String phoneNumber) {

    Random rand = new Random();
    String numStr = String.format("%04d", rand.nextInt(10000));

    smsClient.certifiedPhoneNumber(phoneNumber, numStr);
    String key = prefix + phoneNumber;
    redisClient.setValue(key, numStr, 3L);
    return SendSmsResponse.builder()
        .code(numStr).build();
  }

  public void checkVerifySms(CheckSmsRequest request) {
    String key = prefix + request.getPhoneNumber();
    String value = redisClient.getValue(key);
    if (value == null || !value.equals(request.getCode())) {
      throw new ApiException(ApiExceptionEnum.INVALID_CODE_EXCEPTION);
    }
  }
}