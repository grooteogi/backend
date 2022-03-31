package grooteogi.repository;

import grooteogi.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByEmail(String email);
    User findByUserEmail(String email);

}
