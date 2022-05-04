package grooteogi.service;

import grooteogi.domain.Reservation;
import grooteogi.dto.ReservationDto;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.repository.ReservationRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {
  private ReservationRepository reservationRepository;

  public List<Reservation> getAllReservation() {
    return reservationRepository.findAll();
  }

  public Reservation getReservation(Integer reservationId) {
    Optional<Reservation> reservation = reservationRepository.findById(reservationId);
    if (reservation.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.NOT_FOUND_EXCEPTION);
    }
    return reservation.get();
  }

  public Reservation createReservation(ReservationDto reservationDto) {
    Optional<Reservation> reservation = reservationRepository.findById(reservationDto.getScheduleId());
    if (reservation.isPresent()) {
      throw new ApiException(ApiExceptionEnum.DUPLICATION_VALUE_EXCEPTION);
    }
    Reservation createdReservation = new Reservation();
    BeanUtils.copyProperties(reservationDto, createdReservation);

    return reservationRepository.save(createdReservation);
  }

  public void deleteReservation(Integer reservationId) {
    reservationRepository.deleteById(reservationId);
  }
}
