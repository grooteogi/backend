package grooteogi.repository;

import grooteogi.domain.Reservation;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
  Optional<Reservation> findByScheduleId(Integer scheduleId);

  @Query(
      value = "select * from reservation where host_user_id =:userId and"
          + " status = 0 order by id desc",
      nativeQuery = true
  )
  List<Reservation> findByHostReservation(Integer userId);

  @Query(
      value = "select * from reservation, schedule where host_user_id =:userId and "
          + "reservation.schedule_id = schedule.id and status = 1 and "
          + "schedule.date > CURRENT_DATE order by reservation.id desc",
      nativeQuery = true
  )
  List<Reservation> findByHostProceed(Integer userId);

  @Query(
      value = "select * from reservation, schedule where host_user_id =:userId and "
          + "reservation.schedule_id = schedule.id and status = 1 and "
          + "schedule.date < CURRENT_DATE order by reservation.id desc",
      nativeQuery = true
  )
  List<Reservation> findByHostComplete(Integer userId);

  @Query(
      value = "select * from reservation where host_user_id =:userId order by id desc",
      nativeQuery = true
  )
  List<Reservation> findByHost(Integer userId);

  @Query(
      value = "select * from reservation where participate_user_id =:userId order by id desc",
      nativeQuery = true
  )
  List<Reservation> findByPart(Integer userId);

  @Query(
      value = "select * from reservation, schedule where participate_user_id =:userId and"
          + " reservation.schedule_id = schedule.id and status = 1 and"
          + " schedule.date > CURRENT_DATE order by reservation.id desc",
      nativeQuery = true
  )
  List<Reservation> findByPartProceed(Integer userId);

  @Query(
      value = "select * from reservation, schedule where participate_user_id =:userId and"
          + " reservation.schedule_id = schedule.id and status = 1 and "
          + "schedule.date < CURRENT_DATE order by reservation.id desc",
      nativeQuery = true
  )
  List<Reservation> findByPartComplete(Integer userId);

  @Query(
      value = "select * from reservation where participate_user_id =:userId and"
          + " status = 0 order by id desc",
      nativeQuery = true
  )
  List<Reservation> findByPartReservation(Integer userId);

  @Query(
      value = "select * from reservation, schedule where reservation.schedule_id = schedule.id and"
          + " reservation.id =:id and status = 1 and schedule.date > CURRENT_DATE ",
      nativeQuery = true
  )
  Optional<Reservation> findByUncanceld(@Param("id") Integer id);


  @Query(
      value = "select * from reservation, schedule where reservation.schedule_id = schedule.id and"
          + " schedule.post_id = :id",
      nativeQuery = true
  )
  List<Reservation> findByPostId(@Param("id") Integer id);
}
