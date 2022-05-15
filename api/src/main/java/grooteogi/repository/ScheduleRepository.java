package grooteogi.repository;

import grooteogi.domain.Post;
import grooteogi.domain.Schedule;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

  Optional<Schedule> findById(Integer scheduleId);

  List<Schedule> findByPost(Post post);
}
