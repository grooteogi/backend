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
public class PostService {

  private final PostRepository postRepository;

  public CursorResult<Post> search(String search, Integer cursorId, String type,
      Pageable page) {
    final List<Post> posts;
    if (search == null) {
      posts = searchAllPosts(cursorId, page, type);
    } else {
      posts = searchPosts(search, cursorId, page, type);
    }
    final Integer lastIdOfList = posts.isEmpty() ? null : posts.get(posts.size() - 1).getId();
    return new CursorResult<>(posts, hasNext(lastIdOfList));
  }

  private List<Post> searchAllPosts(Integer cursorId, Pageable page, String type) {
    return cursorId == 0 ? this.postRepository.findAllByOrderByIdDesc(page) :
        this.postRepository.findByIdLessThanOrderByIdDesc(cursorId, page);
  }

  private List<Post> searchPosts(String search, Integer cursorId, Pageable page, String type) {
    return cursorId == 0 ? this.postRepository
        .findByTitleContainingOrContentContainingOrderByIdDesc(search, search, page) :
        this.postRepository.findBySearchOrderByIdDesc(search, search, cursorId, page);
  }

  private Boolean hasNext(Integer lastIdOfList) {
    if (lastIdOfList == null) {
      return false;
    }
    return this.postRepository.existsByIdLessThan(lastIdOfList);
  }
}
