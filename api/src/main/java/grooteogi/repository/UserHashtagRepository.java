package grooteogi.repository;


import grooteogi.domain.UserHashtag;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserHashtagRepository extends JpaRepository<UserHashtag, Integer> {

  UserHashtag findByUserIdAndHashtagId(int userId, int hashtagId);

  List<UserHashtag> findByUserIdAndPage(Integer userId, Pageable page);

  List<UserHashtag> findByUserId(int userId);
}
