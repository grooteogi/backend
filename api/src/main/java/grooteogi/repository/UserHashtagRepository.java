package grooteogi.repository;


import grooteogi.domain.UserHashtag;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserHashtagRepository extends JpaRepository<UserHashtag, Integer> {

  @Query(value = "select * from user_hashtag where user_id = :user_id", nativeQuery = true)
  List<UserHashtag> getAllUserHashtagBy(@Param("user_id") int userId);

  @Query(value = "select * from user_hashtag "
      + "where user_id = :user_id and hashtag_id = :hashtag_id", nativeQuery = true)
  UserHashtag getUserHashtagBy(@Param("user_id") int userId,
      @Param("hashtag_id") int hashtagId);
}
