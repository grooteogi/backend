package grooteogi.repository;

import grooteogi.domain.Heart;
import grooteogi.domain.Post;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HeartRepository extends JpaRepository<Heart, Integer> {

  @Query(
      value = "select * from heart where heart.post_id = :postId and heart.user_id = :userId",
      nativeQuery = true
  )
  Optional<Heart> findByPostIdUserId(@Param("postId") Integer postId,
      @Param("userId") Integer userId);

  List<Heart> findByPost(Post post);
}