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
  
  /*
   * 검색어 존재 여부, cursorId 존재 여부, 정렬 기준
   * */
  public CursorResult<Post> search(String search, Long cursorId, String type,
      Pageable page) {
    final List<Post> posts;
    if (search == null) {
      posts = getAllPosts(cursorId, page, type);
    } else {
      posts = getPosts(search, cursorId, page, type);
    }
    final Long lastIdOfList = posts.isEmpty() ? null : posts.get(posts.size() - 1).getId();

    return new CursorResult<>(posts, hasNext(lastIdOfList));
  }

  /*
   * 검색어가 없는 검색
   * */
  private List<Post> getAllPosts(Long cursorId, Pageable page, String type) {
    if (type.equals("createDate")) {
      return cursorId == 0 ? this.postRepository.findAllByOrderByCreateDateDescIdDesc(page) :
          this.postRepository.findByIdLessThanOrderByCreateDateDescIdDesc(cursorId, page);
    }
    return cursorId == 0 ? this.postRepository.findAllByOrderByIdDesc(page) :
        this.postRepository.findByIdLessThanOrderByIdDesc(cursorId, page);
  }

  /*
   * 검색어가 있는 검색
   * */
  private List<Post> getPosts(String search, Long cursorId, Pageable page, String type) {
    if (type.equals("createDate")) {
      return cursorId == 0 ? this.postRepository.findByTitleContainingOrContentContainingOrderByCreateDateDescIdDesc(search, search, page) :
          this.postRepository.findBySearchOrderByCreateDateDesc(search, search, cursorId, page);
    }
    return cursorId == 0 ? this.postRepository.findByTitleContainingOrContentContainingOrderByIdDesc(search, search, page) :
        this.postRepository.findBySearchOrderByIdDesc(search, search, cursorId, page);
  }

  /*
   * 마지막 id 기준, 뒤에 더 데이터가 있는지 여부 확인
   * */
  private Boolean hasNext(Long lastIdOfList) {
    if (lastIdOfList == null) {
      return false;
    }
    return this.postRepository.existsByIdLessThan(lastIdOfList);
  }
}
