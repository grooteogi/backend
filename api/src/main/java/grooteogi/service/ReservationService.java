package grooteogi.service;

import grooteogi.domain.Reservation;
import grooteogi.dto.ReservationDto;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.repository.ReservationRepository;
import grooteogi.repository.UserRepository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {
  private final ReservationRepository reservationRepository;
  private final UserRepository userRepository;

  public List<Reservation> getAllReservation() {
    List<Reservation> reservations = reservationRepository.findAll();
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

  public Reservation createReservation(ReservationDto reservationDto) {
    Optional<Reservation> reservation = reservationRepository
        .findByScheduleId(reservationDto.getScheduleId());
    if (reservation.isPresent()) {
      throw new ApiException(ApiExceptionEnum.DUPLICATION_RESERVATION_EXCEPTION);
    }

    if (reservation.get().getSchedule() == null) {
      throw new ApiException(ApiExceptionEnum.SCHEDULE_NOT_FOUND_EXCEPTION);
    }

    Reservation createdReservation = new Reservation();
    BeanUtils.copyProperties(reservationDto, createdReservation);
    createdReservation.setCreateDate(Timestamp.valueOf(LocalDateTime.now()));

    return reservationRepository.save(createdReservation);
  }

  public void deleteReservation(Integer reservationId) {
    reservationRepository.deleteById(reservationId);
  }

  public List<Reservation> getUserReservation(Integer userId) {
    List<Reservation> reservations = reservationRepository.findByUserId(userId);
    if (reservations.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.RESERVATION_NOT_FOUND_EXCEPTION);
    }
    return reservations;
  }

}
