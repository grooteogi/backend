package grooteogi.repository;


import grooteogi.domain.UserHashtag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserHashtagRepository extends JpaRepository<UserHashtag, Integer> {

  List<UserHashtag> findByUserId(int userId);

  UserHashtag findByUserIdAndHashtagId(int userId, int hashtagId);
}
