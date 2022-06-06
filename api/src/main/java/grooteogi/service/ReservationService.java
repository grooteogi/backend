package grooteogi.service;

import grooteogi.domain.PostHashtag;
import grooteogi.domain.Reservation;
import grooteogi.domain.Review;
import grooteogi.domain.Schedule;
import grooteogi.domain.User;
import grooteogi.dto.ReservationDto;
import grooteogi.dto.ReservationDto.CheckSmsRequest;
import grooteogi.enums.ReservationStatus;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.mapper.ReservationMapper;
import grooteogi.repository.PostRepository;
import grooteogi.repository.ReservationRepository;
import grooteogi.repository.ReviewRepository;
import grooteogi.repository.ScheduleRepository;
import grooteogi.repository.UserRepository;
import grooteogi.utils.RedisClient;
import grooteogi.utils.SmsClient;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

  private final ReservationRepository reservationRepository;
  private final ScheduleRepository scheduleRepository;
  private final UserRepository userRepository;
  private final PostRepository postRepository;
  private final ReviewRepository reviewRepository;
  private final RedisClient redisClient;

  private final String prefix = "sms_verify";

  private final SmsClient smsClient;

  public ReservationDto.DetailResponse getReservation(Integer reservationId) {
    Optional<Reservation> reservation = reservationRepository.findById(reservationId);
    reservation
        .orElseThrow(() -> new ApiException(ApiExceptionEnum.RESERVATION_NOT_FOUND_EXCEPTION));
    User participateUser = reservation.get().getParticipateUser();

    Optional<String> hostUserPhone = Optional.ofNullable(
        reservation.get().getHostUser().getUserInfo().getContact());
    Optional<String> participateUserPhone = Optional.ofNullable(
        participateUser.getUserInfo().getContact());

    hostUserPhone.orElseThrow(() -> new ApiException(ApiExceptionEnum.CONTACT_NOT_FOUND_EXCEPTION));
    participateUserPhone
        .orElseThrow(() -> new ApiException(ApiExceptionEnum.CONTACT_NOT_FOUND_EXCEPTION));

    Optional<Review> findReview = reviewRepository.findByReservationIdAndUserId(
        reservation.get().getId(), participateUser.getId());

    Review review = findReview.get();
    if (findReview.isEmpty()) {
      review = Review.builder().build();
    }

    return ReservationMapper.INSTANCE.toDetailResponseDto(
        reservation.get(), reservation.get().getSchedule().getPost(),
        reservation.get().getSchedule(), review, hostUserPhone.get(),
        participateUserPhone.get(), participateUser.getNickname());
  }

  public List<ReservationDto.DetailResponse> getReservation(boolean isHost, Integer userId,
      String filter) {

    List<Reservation> reservationList = (isHost) ? reservationRepository.findAllByHostUserId(userId)
        : reservationRepository.findAllByParticipateUserId(userId);

    if (filter == null) {
      return modifyResponseDto(reservationList);
    }

    List<Reservation> dateFilteredList = new ArrayList<>();

    ReservationStatus reservationStatus = ReservationStatus.valueOf(filter);

    List<Reservation> filteredList = reservationList.stream().filter(
        reservation ->
            reservation.getIsCanceled() == (reservationStatus == ReservationStatus.CANCELED)
    ).collect(Collectors.toList());

    if (reservationStatus == ReservationStatus.CANCELED) {
      return modifyResponseDto(filteredList);
    }

    long miliseconds = System.currentTimeMillis();
    Date now = new Date(miliseconds);
    filteredList.forEach(reservation -> {
      Date scheduleDate = reservation.getSchedule().getDate();
      if (reservationStatus == ReservationStatus.COMPLETE) {
        if (now.after(scheduleDate)) {
          dateFilteredList.add(reservation);
        }
      } else {
        if (now.before(scheduleDate)) {
          dateFilteredList.add(reservation);
        }
      }
    });

    return modifyResponseDto(dateFilteredList);

  }

  private List<ReservationDto.DetailResponse> modifyResponseDto(List<Reservation> reservations) {
    if (reservations.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.RESERVATION_NOT_FOUND_EXCEPTION);
    }
    List<ReservationDto.DetailResponse> responseList = new ArrayList<>();
    reservations.forEach(reservation -> {

      Optional<String> hostUserPhone = Optional.ofNullable(
          reservation.getHostUser().getUserInfo().getContact());
      Optional<String> participateUserPhone = Optional.ofNullable(
          reservation.getParticipateUser().getUserInfo().getContact());

      hostUserPhone
          .orElseThrow(() -> new ApiException(ApiExceptionEnum.CONTACT_NOT_FOUND_EXCEPTION));
      participateUserPhone
          .orElseThrow(() -> new ApiException(ApiExceptionEnum.CONTACT_NOT_FOUND_EXCEPTION));

      String participateUserNickname = reservation.getParticipateUser().getNickname();

      Optional<Review> findReview = reviewRepository.findByReservationIdAndUserId(
          reservation.getId(), reservation.getParticipateUser().getId());

      Review review = findReview.get();
      if (findReview.isEmpty()) {
        review = Review.builder().build();
      }

      ReservationDto.DetailResponse detailResponse =
          ReservationMapper.INSTANCE.toDetailResponseDto(
              reservation, reservation.getSchedule().getPost(), reservation.getSchedule(),
              review, hostUserPhone.get(), participateUserPhone.get(), participateUserNickname);
      detailResponse.setHashtags(getTags(reservation.getSchedule().getPost().getPostHashtags()));
      responseList.add(detailResponse);
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
      throw new ApiException(ApiExceptionEnum.SCHEDULE_APPLY_FAIL_EXCEPTION);
    }

    Optional<Schedule> schedule = scheduleRepository.findById(request.getScheduleId());

    schedule.orElseThrow(() -> new ApiException(ApiExceptionEnum.SCHEDULE_NOT_FOUND_EXCEPTION));
    long miliseconds = System.currentTimeMillis();
    Date now = new Date(miliseconds);
    if (now.before(schedule.get().getDate())) {
      throw new ApiException(ApiExceptionEnum.SCHEDULE_APPLY_FAIL_EXCEPTION);
    }

    Optional<User> user = userRepository.findById(userId);

    user.orElseThrow(() -> new ApiException(ApiExceptionEnum.USER_NOT_FOUND_EXCEPTION));

    boolean isWriter = postRepository.existsByUser(user.get());
    if (isWriter) {
      throw new ApiException(ApiExceptionEnum.RESERVATION_HOST_EXCEPTION);
    }

    Reservation createdReservation = ReservationMapper.INSTANCE.toEntity(request, user.get(),
        schedule.get(), false);
    Reservation savedReservation = reservationRepository.save(createdReservation);
    return ReservationMapper.INSTANCE.toResponseDto(savedReservation);
  }

  public void deleteReservation(Integer reservationId, Integer userId) {
    Optional<Reservation> reservation = reservationRepository.findById(reservationId);
    reservation
        .orElseThrow(() -> new ApiException(ApiExceptionEnum.RESERVATION_NOT_FOUND_EXCEPTION));

    int hostId = reservation.get().getHostUser().getId();
    int participateId = reservation.get().getParticipateUser().getId();

    if (hostId != userId && participateId != userId) {
      throw new ApiException(ApiExceptionEnum.NO_PERMISSION_EXCEPTION);
    }

    reservationRepository.delete(reservation.get());
  }

  public ReservationDto.Response modifyStatus(Integer reservationId, Integer userId) {

    Optional<Reservation> reservation = reservationRepository.findUncanceledById(reservationId);

    reservation
        .orElseThrow(() -> new ApiException(ApiExceptionEnum.RESERVATION_NOT_FOUND_EXCEPTION));

    Schedule schedule = reservation.get().getSchedule();

    long miliseconds = System.currentTimeMillis();
    Date now = new Date(miliseconds);

    if (now.before(schedule.getDate())) {
      throw new ApiException(ApiExceptionEnum.NO_MODIFY_EXCEPTION);
    }

    int hostId = reservation.get().getHostUser().getId();
    int participateId = reservation.get().getParticipateUser().getId();

    if (hostId != userId && participateId != userId) {
      throw new ApiException(ApiExceptionEnum.NO_PERMISSION_EXCEPTION);
    }

    Reservation modifiedReservation =
        ReservationMapper.INSTANCE.toModifyIsCanceled(reservation.get(), true);
    reservationRepository.save(modifiedReservation);
    return ReservationMapper.INSTANCE.toResponseDto(modifiedReservation);
  }

  public void sendSms(String phoneNumber) {

    Random rand = new Random();
    String numStr = String.format("%04d", rand.nextInt(10000));

    smsClient.certifiedPhoneNumber(phoneNumber, numStr);
    String key = prefix + phoneNumber;
    redisClient.setValue(key, numStr, 3L);
  }

  public void checkSms(CheckSmsRequest request) {
    String key = prefix + request.getPhoneNumber();
    String value = redisClient.getValue(key);
    if (value == null || !value.equals(request.getCode())) {
      throw new ApiException(ApiExceptionEnum.INVALID_CODE_EXCEPTION);
    }
  }
}