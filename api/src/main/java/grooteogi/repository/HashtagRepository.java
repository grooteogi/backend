package grooteogi.repository;

import grooteogi.domain.Hashtag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HashtagRepository extends JpaRepository<Hashtag, Integer> {

  @Query(value = "select * from hashtag where hashtag_type = :type "
      + "group by id order by count desc limit 10", nativeQuery = true)
  List<Hashtag> getTopTenHashtag(@Param("type") String type);


  @Query(value = "select * from hashtag where tag = :tag ", nativeQuery = true)
  Hashtag findByTag(@Param("tag") String tag);
}
