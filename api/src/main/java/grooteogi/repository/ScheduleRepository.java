package grooteogi.repository;

import grooteogi.domain.Post;
import grooteogi.domain.Schedule;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

  Optional<Schedule> findById(Integer scheduleId);

  List<Schedule> findByPost(Post post);

  @Query(
      value = "select * from schedule where schedule.post_id = :id",
      nativeQuery = true
  )
  List<Schedule> findByPostId(@Param("id") Integer id);
}
