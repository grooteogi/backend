package grooteogi.service;

import grooteogi.domain.Post;
import grooteogi.repository.PostRepository;
import grooteogi.utils.CursorResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PageService {

  private final PostRepository postRepository;

  public CursorResult<Post> search(String search, Long cursorId, Pageable page) {
    final List<Post> posts;
    if (search == null) {
      posts = getAllPosts(cursorId, page);
    } else {
      posts = getPosts(search, cursorId, page);
    }
    final Long lastIdOfList = posts.isEmpty() ? null : posts.get(posts.size() - 1).getId();

    return new CursorResult<>(posts, hasNext(lastIdOfList));
  }

  private List<Post> getAllPosts(Long cursorId, Pageable page) {
    return cursorId == 0 ? this.postRepository.findAllByOrderByIdDesc(page) :
        this.postRepository.findByIdLessThanOrderByIdDesc(cursorId, page);
  }

  private List<Post> getPosts(String search, Long cursorId, Pageable page) {
    return cursorId == 0 ? this.postRepository.findAllByOrderByIdDesc(search, search, page) :
        this.postRepository.findBySearchOrderByIdDesc(search, search, cursorId, page);
  }

  private Boolean hasNext(Long lastIdOfList) {
    if (lastIdOfList == null) {
      return false;
    }
    return this.postRepository.existsByIdLessThan(lastIdOfList);
  }
}
