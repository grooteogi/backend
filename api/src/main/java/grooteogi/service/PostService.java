package grooteogi.service;

import grooteogi.domain.Hashtag;
import grooteogi.domain.Post;
import grooteogi.domain.PostHashtag;
import grooteogi.domain.Schedule;
import grooteogi.domain.User;
import grooteogi.dto.PostDto;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.mapper.PostMapper;
import grooteogi.mapper.ScheduleMapper;
import grooteogi.repository.HashtagRepository;
import grooteogi.repository.PostHashtagRepository;
import grooteogi.repository.PostRepository;
import grooteogi.repository.UserRepository;
import java.util.ArrayList;
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

  public PostDto.Response getPost(int postId) {

    if (this.postRepository.findById(postId).isEmpty()) {
      throw new ApiException(ApiExceptionEnum.POST_NOT_FOUND_EXCEPTION);
    }

    //조회수 증가
    Optional<Post> post = this.postRepository.findById(postId);
    post.get().setViews(post.get().getViews() + 1);
    Post updatePost = this.postRepository.save(post.get());
    PostDto.Response response = PostMapper.INSTANCE.toResponseDto(updatePost);
    return response;
  }

  public PostDto.Response createPost(PostDto.Request request) {
    //변수 정의
    Optional<User> user = this.userRepository.findById(request.getUserId());

    //Post Hashtag 저장
    List<PostHashtag> postHashtags = new ArrayList<>();
    Arrays.stream(request.getHashtags()).forEach(name -> {
      Hashtag hashtag = this.hashtagRepository.findByTag(name);
      hashtag.setCount(hashtag.getCount() + 1);
      PostHashtag createdPostHashtag = PostHashtag.builder()
          .hashTag(hashtag)
          .build();

      postHashtags.add(createdPostHashtag);
    });

    // Schedule 저장
    List<Schedule> schedules = new ArrayList<>();
    request.getSchedules().forEach(schedule -> {
      Schedule createdSchedule = ScheduleMapper.INSTANCE.toEntity(schedule);

      schedules.add(createdSchedule);
    });

    Post created = this.postRepository
        .save(PostMapper.INSTANCE.toEntity(request, user.get(), postHashtags, schedules));
    PostDto.Response response = PostMapper.INSTANCE.toResponseDto(created);

    return response;
  }

  @Transactional
  public PostDto.Response modifyPost(PostDto.Request request, int postId) {
    Optional<Post> post = this.postRepository.findById(postId);

    //hashtag count - 1
    List<PostHashtag> postHashtagList = post.get().getPostHashtags();

    postHashtagList.forEach(postHashtag -> {
      Hashtag beforeHashtag = postHashtag.getHashTag();
      beforeHashtag.setCount(beforeHashtag.getCount() - 1);
      this.postHashtagRepository.delete(postHashtag);
    });

    post.get().getPostHashtags().clear();

    //PostHashtag 저장
    String[] hashtags = request.getHashtags();

    Arrays.stream(hashtags).forEach(name -> {
      PostHashtag modifiedPostHashtag = new PostHashtag();
      Hashtag hashtag = this.hashtagRepository.findByTag(name);

      hashtag.setCount(hashtag.getCount() + 1);

      modifiedPostHashtag.setHashTag(hashtag);
      modifiedPostHashtag.setPost(post.get());

      post.get().getPostHashtags().add(modifiedPostHashtag);
    });

    Post modifiedPost = postRepository.save(PostMapper.INSTANCE.toModify(post.get(), request));
    PostDto.Response response = PostMapper.INSTANCE.toResponseDto(modifiedPost);
    return response;
  }

  public List<PostDto.Response> deletePost(int postId) {
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

    List<PostDto.Response> responses = new ArrayList<>();
    this.postRepository.findAll().forEach(result -> responses
        .add(PostMapper.INSTANCE.toResponseDto(result)));
    return responses;
  }

  public List<PostDto.Response> search(String search, String type,
      Pageable page) {
    final List<PostDto.Response> posts;
    if (search == null) {
      posts = searchAllPosts(page, type);
    } else {
      posts = searchPosts(search, page, type);
    }
    return posts;
  }


  public List<PostDto.Response> searchAllPosts(Pageable page, String type) {
    List<PostDto.Response> responses = new ArrayList<>();
    this.postRepository.findAllByPage(page).forEach(result -> responses
        .add(PostMapper.INSTANCE.toResponseDto(result)));
    return responses;
  }

  private List<PostDto.Response> searchPosts(String search, Pageable page, String type) {
    List<PostDto.Response> responses = new ArrayList<>();
    this.postRepository.findBySearch(search, search, page).forEach(result -> responses
        .add(PostMapper.INSTANCE.toResponseDto(result)));
    return responses;
  }

}
