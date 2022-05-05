package grooteogi.repository;


import grooteogi.domain.UserHashtag;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserHashtagRepository extends JpaRepository<UserHashtag, Integer> {

  UserHashtag findByUserIdAndHashtagId(int userId, int hashtagId);

  @Query(
      value = "SELECT * FROM user_hashtag WHERE user_id = :userId",
      nativeQuery = true
  )
  List<UserHashtag> findByUserIdAndPage(@Param("userId") Integer userId, Pageable page);

  List<UserHashtag> findByUserId(int userId);

  @Query(
      value = "SELECT * FROM user_hashtag",
      nativeQuery = true
  )
  List<UserHashtag> findAllByPage(Pageable page);
}
