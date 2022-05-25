package grooteogi.repository;

import grooteogi.domain.Post;
import grooteogi.domain.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Integer> {

  Boolean existsByUser(User user);

  @Query(
      value = "SELECT * FROM post WHERE (title LIKE %:keyword% OR content LIKE %:keyword%) "
          + "ORDER BY id DESC",
      nativeQuery = true
  )
  Page<Post> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);

}
