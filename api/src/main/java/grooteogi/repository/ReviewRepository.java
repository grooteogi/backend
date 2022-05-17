package grooteogi.repository;

import grooteogi.domain.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

  @Query(
      value = "select * from review where schedule.post_id = :id",
      nativeQuery = true
  )
  List<Review> findByPostId(Integer postId);
}
