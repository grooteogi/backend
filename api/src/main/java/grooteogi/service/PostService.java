package grooteogi.service;

import grooteogi.domain.Hashtag;
import grooteogi.domain.Post;
import grooteogi.domain.PostHashtag;
import grooteogi.domain.Schedule;
import grooteogi.domain.User;
import grooteogi.dto.PostDto;
import grooteogi.dto.PostDto.Response;
import grooteogi.dto.ScheduleDto;
import grooteogi.enums.PostFilterEnum;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.mapper.PostMapper;
import grooteogi.repository.HashtagRepository;
import grooteogi.repository.PostHashtagRepository;
import grooteogi.repository.PostRepository;
import grooteogi.repository.ScheduleRepository;
import grooteogi.repository.UserRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
  private final ScheduleRepository scheduleRepository;

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

  private List<Schedule> createSchedule(List<ScheduleDto.Request> requests) {
    List<Schedule> schedules = PostMapper.INSTANCE.toScheduleEntities(requests);

    schedules.forEach(schedule -> {
      scheduleRepository.save(schedule);
    });
    return schedules;
  }

  public PostDto.Response createPost(PostDto.Request request) {

    List<Schedule> requests = createSchedule(request.getSchedules());

    Optional<User> user = this.userRepository.findById(request.getUserId());

    List<PostHashtag> postHashtags = new ArrayList<>();
    Arrays.stream(request.getHashtags()).forEach(name -> {
      Optional<Hashtag> hashtag = this.hashtagRepository.findByName(name);
      hashtag.ifPresent(tag -> {
        tag.setCount(tag.getCount() + 1);
        PostHashtag createdPostHashtag = PostHashtag.builder()
            .hashTag(tag)
            .build();
        postHashtags.add(createdPostHashtag);
      });
    });

    Post createdPost = PostMapper.INSTANCE.toEntity(request, user.get(), postHashtags);
    createdPost.setSchedules(requests);

    Post savedPost = postRepository.save(createdPost);

    requests.forEach(schedule -> {
      schedule.setPost(savedPost);
      scheduleRepository.save(schedule);
    });

    PostDto.Response response = PostMapper.INSTANCE.toResponseDto(savedPost);
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
      Optional<Hashtag> hashtag = this.hashtagRepository.findByName(name);

      hashtag.ifPresent(tag -> {
        tag.setCount(tag.getCount() + 1);
        modifiedPostHashtag.setHashTag(tag);
        modifiedPostHashtag.setPost(post.get());

        post.get().getPostHashtags().add(modifiedPostHashtag);
      });
    });

    Post modifiedPost = PostMapper.INSTANCE.toModify(post.get(), request);
    postRepository.save(modifiedPost);
    PostDto.Response response = PostMapper.INSTANCE.toResponseDto(modifiedPost);
    return response;
  }

  public void deletePost(int postId) {
    Optional<Post> post = this.postRepository.findById(postId);

    if (post.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.POST_NOT_FOUND_EXCEPTION);
    }

    List<PostHashtag> postHashtagList = post.get().getPostHashtags();

    postHashtagList.forEach(postHashtag -> {
      Optional<Hashtag> hashtag = this.hashtagRepository.findById(postHashtag.getHashTag().getId());
      hashtag.get().setCount(hashtag.get().getCount() - 1);
    });

    List<Schedule> schedules = scheduleRepository.findByPost(post.get());

    if (schedules.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.NOT_FOUND_EXCEPTION);
    }
    schedules.forEach(schedule -> scheduleRepository.delete(schedule));
    this.postRepository.delete(post.get());
  }

  public List<PostDto.Response> search(String keyword, String filter,
      Pageable page) {
    return keyword == null ? searchAllPosts(page, filter) : searchPosts(keyword, page, filter);
  }

  private List<Response> filter(List<Post> postList, String filter) {

    PostFilterEnum postFilterEnum = PostFilterEnum.valueOf(filter);

    if (postFilterEnum == PostFilterEnum.VIEWS) {
      List<Post> filteredPostList = postList.stream()
          .sorted(Comparator.comparingInt(Post::getViews).reversed())
          .collect(Collectors.toList());
      return PostMapper.INSTANCE.toResponseListDto(filteredPostList);
    } else if (postFilterEnum == PostFilterEnum.POPULAR) {
      List<Post> filteredPostList = postList.stream()
          .filter(post -> post.getHearts().size() > 0)
          .sorted(Comparator.comparingInt(post -> post.getHearts().size()))
          .collect(Collectors.toList());
      Collections.reverse(filteredPostList);
      return PostMapper.INSTANCE.toResponseListDto(filteredPostList);
    }

    return PostMapper.INSTANCE.toResponseListDto(postList);
  }

  public List<PostDto.Response> searchAllPosts(Pageable page, String filter) {
    List<Post> posts = postRepository.findAll();

    return filter(posts, filter);
  }

  private List<PostDto.Response> searchPosts(String keyword, Pageable page, String filter) {
    List<Post> posts = postRepository.findAllByKeyword(keyword, page);

    return filter(posts, filter);


  }
}