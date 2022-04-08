package grooteogi.repository;

import grooteogi.domain.PostHashtag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostHashtagRepository extends JpaRepository<PostHashtag, Integer> {


  List<PostHashtag> findByPostId(int postId);

}
