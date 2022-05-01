package grooteogi.service;

import grooteogi.domain.Hashtag;
import grooteogi.domain.Post;
import grooteogi.domain.PostHashtag;
import grooteogi.domain.User;
import grooteogi.dto.PostDto;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.repository.HashtagRepository;
import grooteogi.repository.PostHashtagRepository;
import grooteogi.repository.PostRepository;
import grooteogi.repository.UserRepository;
import grooteogi.utils.CursorResult;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final PostHashtagRepository postHashtagRepository;
  private final UserRepository userRepository;
  private final HashtagRepository hashtagRepository;

  public List<Post> getAllPost() {
    return this.postRepository.findAll();
  }

  public Post getPost(int postId) {

    if (this.postRepository.findById(postId).isEmpty()) {
      throw new ApiException(ApiExceptionEnum.POST_NOT_FOUND_EXCEPTION);
    }

    //조회수 증가
    Optional<Post> post = this.postRepository.findById(postId);
    post.get().setViews(post.get().getViews() + 1);
    this.postRepository.save(post.get());

    return post.get();
  }

  public Post createPost(PostDto postDto) {
    //변수 정의
    Post createdPost = new Post();
    Optional<User> user = this.userRepository.findById(postDto.getUserId());

    //Post 저장
    createdPost.setUser(user.get());
    createdPost.setTitle(postDto.getTitle());
    createdPost.setContent(postDto.getContent());
    createdPost.setCreateDate(Timestamp.valueOf(LocalDateTime.now()));
    createdPost.setModifiedDate(Timestamp.valueOf(LocalDateTime.now()));
    createdPost.setImageUrl(postDto.getImageUrl());

    //Post Hashtag 저장
    Arrays.stream(postDto.getHashtags()).forEach(name -> {
      PostHashtag createdPostHashtag = new PostHashtag();
      Hashtag hashtag = this.hashtagRepository.findByTag(name);

      hashtag.setCount(hashtag.getCount() + 1);

      createdPostHashtag.setHashTag(hashtag);
      createdPostHashtag.setRegistered(Timestamp.valueOf(LocalDateTime.now()));
      createdPostHashtag.setPost(createdPost);

      createdPost.getPostHashtags().add(createdPostHashtag);
    });

    this.postRepository.save(createdPost);

    return createdPost;
  }

  @Transactional
  public Post modifyPost(PostDto modifiedDto, int postId) {
    Optional<Post> modifiedPost = this.postRepository.findById(postId);

    //Post 데이터 처리
    modifiedPost.get().setTitle(modifiedDto.getTitle());
    modifiedPost.get().setContent(modifiedDto.getContent());
    modifiedPost.get().setImageUrl(modifiedDto.getImageUrl());
    modifiedPost.get().setModifiedDate(Timestamp.valueOf(LocalDateTime.now()));

    //hashtag count - 1
    List<PostHashtag> postHashtagList = modifiedPost.get().getPostHashtags();

    postHashtagList.forEach(postHashtag -> {
      Hashtag beforeHashtag = postHashtag.getHashTag();
      beforeHashtag.setCount(beforeHashtag.getCount() - 1);
      this.postHashtagRepository.delete(postHashtag);
    });

    modifiedPost.get().getPostHashtags().clear();

    //PostHashtag 저장
    String[] hashtags = modifiedDto.getHashtags();

    Arrays.stream(hashtags).forEach(name -> {
      PostHashtag modifiedPostHashtag = new PostHashtag();
      Hashtag hashtag = this.hashtagRepository.findByTag(name);

      hashtag.setCount(hashtag.getCount() + 1);

      modifiedPostHashtag.setHashTag(hashtag);
      modifiedPostHashtag.setRegistered(Timestamp.valueOf(LocalDateTime.now()));
      modifiedPostHashtag.setPost(modifiedPost.get());

      modifiedPost.get().getPostHashtags().add(modifiedPostHashtag);
    });

    return modifiedPost.get();
  }

  public List<Post> deletePost(int postId) {
    Optional<Post> post = this.postRepository.findById(postId);

    if (post.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.POST_NOT_FOUND_EXCEPTION);
    }

    List<PostHashtag> postHashtagList = post.get().getPostHashtags();

    postHashtagList.forEach(postHashtag -> {
      Optional<Hashtag> hashtag = this.hashtagRepository.findById(postHashtag.getHashTag().getId());
      hashtag.get().setCount(hashtag.get().getCount() - 1);
    });

    this.postRepository.delete(post.get());

    return this.postRepository.findAll();
  }

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
