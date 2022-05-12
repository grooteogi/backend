package grooteogi.repository;

import grooteogi.domain.Hashtag;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HashtagRepository extends JpaRepository<Hashtag, Integer> {

  @Query(value = "select * from hashtag order by count desc limit :size", nativeQuery = true)
  List<Hashtag> findByCount(Integer size);

  @Query(value = "select * from hashtag where name LIKE %:keyword% order by count desc",
      nativeQuery = true)
  List<Hashtag> findAllByName(String keyword);

  @Query(value = "select * from hashtag where name=:keyword", nativeQuery = true)
  Optional<Hashtag> findByName(String keyword);


}
