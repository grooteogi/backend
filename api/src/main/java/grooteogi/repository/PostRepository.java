package grooteogi.repository;

import grooteogi.domain.Post;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer> {

  Page<Post> findAllByTitleContainingOrContentContaining(String title,
      String content, Pageable pageable);

  List<Post> findByUserId(Integer userId);

}
