package grooteogi.repository;

import grooteogi.domain.Post;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Integer> {

  @Query(
      value = "SELECT * FROM post WHERE (title LIKE %:title% OR content LIKE %:content%)",
      nativeQuery = true
  )
  List<Post> findBySearch(@Param("title") String title,
      @Param("content") String content, Pageable pageable);

  Boolean existsByIdLessThan(Integer id);


  //List<Post> findAllBy(Pageable page);
}
