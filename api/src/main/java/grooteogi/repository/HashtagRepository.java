package grooteogi.repository;

import grooteogi.domain.Hashtag;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HashtagRepository extends JpaRepository<Hashtag, Integer> {


  @Query(value = "select * from hashtag where hashtag_type = :type "
      + "order by count desc ", nativeQuery = true)
  List<Hashtag> findByTypeAndPage(@Param("type") String type, Pageable page);

  @Query(value = "select * from hashtag order by count desc", nativeQuery = true)
  List<Hashtag> findAllByPage(Pageable page);

  Hashtag findByTag(String tag);
}
