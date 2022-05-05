package grooteogi.service;

import grooteogi.domain.Reservation;
import grooteogi.domain.Schedule;
import grooteogi.dto.ReservationDto;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.repository.ReservationRepository;
import grooteogi.repository.ScheduleRepository;
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
  private final ScheduleRepository scheduleRepository;

  public List<Reservation> getAllReservation() {
    List<Reservation> reservations = reservationRepository.findAll();
    if(reservations.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.NOT_FOUND_EXCEPTION);
    }
    return reservations;
  }

  public Reservation getReservation(Integer reservationId) {
    Optional<Reservation> reservation = reservationRepository.findById(reservationId);
    if (reservation.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.NOT_FOUND_EXCEPTION);
    }
    return reservation.get();
  }

  public Reservation createReservation(ReservationDto reservationDto) {
    Optional<Schedule> schedule = scheduleRepository.findById(reservationDto.getScheduleId());
    if (schedule.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.NOT_FOUND_EXCEPTION);
    }
    Optional<Reservation> reservation = reservationRepository.findByScheduleId(schedule.get().getId());
    if(reservation.isPresent()) {
      throw new ApiException(ApiExceptionEnum.DUPLICATION_RESERVATION_EXCEPTION);
    }
    Reservation createdReservation = new Reservation();
    BeanUtils.copyProperties(reservationDto, createdReservation);
    createdReservation.setCreateDate(Timestamp.valueOf(LocalDateTime.now()));

    return reservationRepository.save(createdReservation);
  }

  public void deleteReservation(Integer reservationId) {
    reservationRepository.deleteById(reservationId);
  }
}
