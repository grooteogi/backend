package grooteogi.repository;

import grooteogi.domain.Hashtag;
import grooteogi.domain.HashtagType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HashtagRepository extends JpaRepository<Hashtag, Integer> {
    @Query(
            value = "select * from hashtag where id in (select hashtag_id from user_hashtag group by hashtag_id order by count(hashtag_id) desc) and hashtag_type = :type limit 10",
            nativeQuery = true
    )
    List<Hashtag> getTopTenHashtag(@Param("type")String type);
}
