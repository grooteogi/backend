package grooteogi.repository;

import grooteogi.domain.Schedule;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

  Optional<Schedule> findById(Integer scheduleId);
}
