package grooteogi.repository;

import grooteogi.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer> {

  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);

  boolean existsByNickname(String nickname);

  @Query(
      value = "select * from user join user_info on user_info_id = :id",
      nativeQuery = true
  )
  Optional<User> findProfileById(@Param("id") Integer id);
}
