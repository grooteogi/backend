package grooteogi.repository;

import grooteogi.domain.Reservation;
import grooteogi.enums.ReservationType;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
  Optional<Reservation> findByScheduleId(Integer scheduleId);

  List<Reservation> findByParticipateUserId(Integer userId);
  
  List<Reservation> findByHostUserId(int userId);

  List<Reservation> findByHostUserIdAndStatusOrderByIdDesc(int userId, ReservationType status);

  List<Reservation> findByParticipateUserIdAndStatusOrderByIdDesc(Integer userId,
      ReservationType status);

  @Query(value = "select * from reservation where Reservation.hostUser.id =:userId and Reservation.status =:status order by Reservation.id desc",
      nativeQuery = true)
  List<Reservation> findHostReservationCancel(@Param("userId") Integer userId,
      @Param("status") ReservationType status);

  @Query(value = "select * from reservation where Reservation.participateUser.id =:userId and Reservation.status =:status order by Reservation.id desc",
      nativeQuery = true)
  List<Reservation> findParticipateReservationCancel(@Param("userId") Integer userId,
      @Param("status") ReservationType status);
}
