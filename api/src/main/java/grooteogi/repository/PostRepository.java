package grooteogi.repository;

import grooteogi.domain.Post;
import grooteogi.domain.User;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Integer> {

  Boolean existsByUser(User user);

  @Query(
      value = "SELECT * FROM post WHERE (title LIKE %:keyword% OR content LIKE %:keyword%)",
      nativeQuery = true
  )
  List<Post> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);


}
