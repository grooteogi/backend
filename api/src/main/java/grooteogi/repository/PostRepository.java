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
      value = "SELECT * FROM post WHERE (title LIKE %:title% OR content LIKE %:content%)",
      nativeQuery = true
  )
  List<Post> findBySearch(@Param("title") String title,
      @Param("content") String content, Pageable pageable);

  @Query(
      value = "SELECT * FROM post",
      nativeQuery = true
  )
  List<Post> findAllByPage(Pageable pageable);

  @Query(
      value = "select post.id, post.content, post.create_at, post.credit, "
          + "post.image_url, post.title, post.update_at, post.views, post.user_id "
          + "from post inner join heart h on post.id = h.post_id "
          + "group by post.id order by count(post.id)",
      nativeQuery = true
  )
  List<Post> findAllByHeart(Pageable pageable);

  @Query(
      value = "select post.id, post.content, post.create_at, post.credit, "
          + "post.image_url, post.title, post.update_at, post.views, post.user_id "
          + "from post inner join review r on post.id = r.post_id "
          + "group by post.id order by count(post.id);",
      nativeQuery = true
  )
  List<Post> findAllByReview(Pageable page);

  @Query(
      value = "select post.id, post.content, post.create_at, post.credit, "
          + "post.image_url, post.title, post.update_at, post.views, post.user_id "
          + "from post inner join heart h on post.id = h.post_id "
          + "WHERE (title LIKE %:title% OR content LIKE %:content%) "
          + "group by post.id order by count(post.id)",
      nativeQuery = true
  )
  List<Post> findBySearchAndHeart(@Param("title") String title,
      @Param("content") String content, Pageable pageable);


  @Query(
      value = "select post.id, post.content, post.create_at, post.credit, "
          + "post.image_url, post.title, post.update_at, post.views, post.user_id "
          + "from post inner join review r on post.id = r.post_id "
          + "WHERE (title LIKE %:title% OR content LIKE %:content%) "
          + "group by post.id order by count(post.id)",
      nativeQuery = true
  )
  List<Post> findBySearchAndReview(@Param("title") String title,
      @Param("content") String content, Pageable pageable);
}
