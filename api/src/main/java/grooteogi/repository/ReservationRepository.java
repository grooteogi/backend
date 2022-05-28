package grooteogi.repository;

import grooteogi.domain.Reservation;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
  Optional<Reservation> findByScheduleId(Integer scheduleId);

  List<Reservation> findAllByParticipateUserId(Integer userId);

  @Query(
      value = "select * from reservation, schedule where reservation.schedule_id = schedule.id and"
          + " schedule.post_id = :id",
      nativeQuery = true
  )
  List<Reservation> findByPostId(@Param("id") Integer id);

  @Query(
      value = "select * from reservation "
          + "where is_canceled = false and reservation.id =:reservationId",
      nativeQuery = true
  )
  Optional<Reservation> findUncanceledById(@Param("reservationId") Integer reservationId);

  List<Reservation> findAllByHostUserId(Integer userId);
}
