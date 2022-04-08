package grooteogi.repository;

import grooteogi.domain.Post;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {
  @Query(
      value = "SELECT * FROM post WHERE title LIKE %:title% OR"
          + " content LIKE %:content% ORDER BY id desc", nativeQuery = true
  )
  List<Post> findAllByOrderByIdDesc(@Param("title") String title, @Param("content") String content,
      Pageable pageable);

  @Query(
      value = "SELECT * FROM post WHERE (title LIKE %:title% OR content LIKE %:content%)"
          + "AND id < :id ORDER BY id desc", nativeQuery = true
  )
  List<Post> findBySearchOrderByIdDesc(@Param("title") String title,
      @Param("content") String content,
      Long id, Pageable pageable);

  Boolean existsByIdLessThan(Long id);

  List<Post> findByIdLessThanOrderByIdDesc(Long cursorId, Pageable page);

  List<Post> findAllByOrderByIdDesc(Pageable page);

}
