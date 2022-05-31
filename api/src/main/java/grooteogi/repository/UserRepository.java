package grooteogi.repository;

import grooteogi.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);

  boolean existsByNickname(String nickname);

  Optional<User> findById(Integer userId);
}
