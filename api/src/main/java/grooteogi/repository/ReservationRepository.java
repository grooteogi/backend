package grooteogi.repository;

import grooteogi.domain.Reservation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
  Optional<Reservation> findByScheduleId(Integer scheduleId);

  List<Reservation> findByUserId(Integer userId);

  @Query("select r, s, p from Post as p left outer join Schedule as s on p.id = s.post.id "
      + "inner join Reservation as r on s.id = r.schedule.id")
  List<Object[]> findPostReservation();
}
