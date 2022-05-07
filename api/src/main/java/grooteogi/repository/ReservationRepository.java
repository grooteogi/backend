package grooteogi.repository;

import grooteogi.domain.Reservation;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
  Optional<Reservation> findByScheduleId(Integer scheduleId);

  List<Reservation> findByUserId(Integer userId);

  @Query("select r, s, p from Post as p left outer join Schedule as s on p.id = s.post.id "
      + "inner join Reservation as r on s.id = r.schedule.id where p.user.id = :id")
  List<Object[]> findPostReservation(@Param("id") int id);
}
