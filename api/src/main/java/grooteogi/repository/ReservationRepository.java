package grooteogi.repository;

import grooteogi.domain.Reservation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
  Optional<Reservation> findByScheduleId(Integer scheduleId);
}
