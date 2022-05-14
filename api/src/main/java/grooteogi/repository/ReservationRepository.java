package grooteogi.repository;

import grooteogi.domain.Reservation;
import grooteogi.enums.ReservationType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
  Optional<Reservation> findByScheduleId(Integer scheduleId);

  List<Reservation> findByHostUserIdAndStatusOrderByIdDesc(int userId, ReservationType status);

  List<Reservation> findByParticipateUserIdAndStatusOrderByIdDesc(Integer userId,
      ReservationType status);

}
