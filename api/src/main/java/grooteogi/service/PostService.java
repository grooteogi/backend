package grooteogi.service;

import grooteogi.domain.Hashtag;
import grooteogi.domain.Post;
import grooteogi.domain.PostHashtag;
import grooteogi.domain.Reservation;
import grooteogi.domain.Schedule;
import grooteogi.domain.User;
import grooteogi.domain.UserInfo;
import grooteogi.dto.PostDto;
import grooteogi.dto.PostDto.Request;
import grooteogi.dto.ScheduleDto;
import grooteogi.dto.HashtagDto;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.mapper.HashtagMapper;
import grooteogi.mapper.PostMapper;
import grooteogi.repository.HashtagRepository;
import grooteogi.repository.PostHashtagRepository;
import grooteogi.repository.PostRepository;
import grooteogi.repository.ReservationRepository;
import grooteogi.repository.ReviewRepository;
import grooteogi.repository.ScheduleRepository;
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
  private final ScheduleRepository scheduleRepository;
  private final ReviewRepository reviewRepository;
  private final ReservationRepository reservationRepository;

  public PostDto.Response getPostResponse(Integer postId) {

    if (this.postRepository.findById(postId).isEmpty()) {
      throw new ApiException(ApiExceptionEnum.POST_NOT_FOUND_EXCEPTION);
    }

    //조회수 증가
    Optional<Post> post = postRepository.findById(postId);
    post.get().setViews(post.get().getViews() + 1);
    Post updatePost = postRepository.save(post.get());

    PostDto.Response result = PostMapper.INSTANCE.toDetailResponse(updatePost);

    User user = post.get().getUser();
    result.setMentor(PostMapper.INSTANCE.toUserResponse(user, user.getUserInfo()));
    return result;
  }

  private List<Schedule> createSchedule(List<ScheduleDto.Request> requests) {
    List<Schedule> schedules = PostMapper.INSTANCE.toScheduleEntities(requests);

    scheduleRepository.saveAll(schedules);
    return schedules;
  }

  private List<String> getPostHashtags(List<PostHashtag> postHashtags) {
    List<String> response = new ArrayList<>();
    postHashtags.forEach(postHashtag -> response.add(postHashtag.getHashTag().getName()));
    return response;
  }

  private List<PostHashtag> createPostHashtag(String[] postHashtags) {
    List<PostHashtag> postHashtagList = new ArrayList<>();
    Arrays.stream(postHashtags).forEach(name -> {
      Optional<Hashtag> hashtag = hashtagRepository.findByName(name);
      hashtag.ifPresent(tag -> {
        tag.setCount(tag.getCount() + 1);
        PostHashtag createdPostHashtag = PostHashtag.builder()
            .hashTag(tag)
            .build();
        postHashtagRepository.save(createdPostHashtag);
        postHashtagList.add(createdPostHashtag);
      });
    });
    return postHashtagList;
  }

  public PostDto.CreateResponse createPost(PostDto.Request request, Integer userId) {

    List<Schedule> requests = createSchedule(request.getSchedules());

    List<PostHashtag> postHashtags = createPostHashtag(request.getHashtags());

    Optional<User> user = userRepository.findById(userId);

    Post createdPost = PostMapper.INSTANCE.toEntity(request, user.get());
    createdPost.setSchedules(requests);
    createdPost.setPostHashtags(postHashtags);

    Post savedPost = postRepository.save(createdPost);

    requests.forEach(schedule -> {
      schedule.setPost(savedPost);
      scheduleRepository.save(schedule);
    });

    postHashtags.forEach(postHashtag -> {
      postHashtag.setPost(savedPost);
      postHashtagRepository.save(postHashtag);
    });

    return PostMapper.INSTANCE.toCreateResponseDto(savedPost);
  }

  @Transactional
  public PostDto.Response modifyPost(Request request, Integer postId) {
    Optional<Post> post = postRepository.findById(postId);

    List<PostHashtag> postHashtagList = post.get().getPostHashtags();

    postHashtagList.forEach(postHashtag -> {
      Hashtag beforeHashtag = postHashtag.getHashTag();
      beforeHashtag.setCount(beforeHashtag.getCount() - 1);
      this.postHashtagRepository.delete(postHashtag);
    });

    post.get().getPostHashtags().clear();

    String[] hashtags = request.getHashtags();

    Arrays.stream(hashtags).forEach(name -> {
      PostHashtag modifiedPostHashtag = new PostHashtag();
      Optional<Hashtag> hashtag = hashtagRepository.findByName(name);

      hashtag.ifPresent(tag -> {
        tag.setCount(tag.getCount() + 1);
        modifiedPostHashtag.setHashTag(tag);
        modifiedPostHashtag.setPost(post.get());

        post.get().getPostHashtags().add(modifiedPostHashtag);
      });
    });

    Post modifiedPost = PostMapper.INSTANCE.toModify(post.get(), request);
    postRepository.save(modifiedPost);
    return PostMapper.INSTANCE.toDetailResponse(modifiedPost);
  }

  public void deletePost(Integer postId) {
    Optional<Post> post = postRepository.findById(postId);

    if (post.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.POST_NOT_FOUND_EXCEPTION);
    }

    List<PostHashtag> postHashtagList = post.get().getPostHashtags();

    postHashtagList.forEach(postHashtag -> {
      Optional<Hashtag> hashtag = hashtagRepository.findById(postHashtag.getHashTag().getId());
      hashtag.get().setCount(hashtag.get().getCount() - 1);
    });

    List<Reservation> reservations = reservationRepository.findByPostId(post.get().getId());
    reservationRepository.deleteAll(reservations);

    List<Schedule> schedules = scheduleRepository.findByPost(post.get());

    if (schedules.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.NOT_FOUND_EXCEPTION);
    }
    scheduleRepository.deleteAll(schedules);
    this.postRepository.delete(post.get());
  }

  public List<PostDto.SearchResponse> search(String keyword, String sort,
      Pageable page) {
    final List<PostDto.SearchResponse> posts;
    if (keyword == null) {
      posts = searchAllPosts(page, sort);
    } else {
      posts = searchPosts(keyword, page, sort);
    }
    return posts;
  }


  public List<PostDto.SearchResponse> searchAllPosts(Pageable page, String sort) {
    List<PostDto.SearchResponse> responses = new ArrayList<>();
    this.postRepository.findAllByPage(page).forEach(result -> {
      PostDto.SearchResponse response = PostMapper.INSTANCE.toSearchResponseDto(result);
      response.setHashtags(getPostHashtags(result.getPostHashtags()));
      responses.add(response);
    });
    return responses;
  }

  private List<PostDto.SearchResponse> searchPosts(String keyword, Pageable page, String sort) {
    List<PostDto.SearchResponse> responses = new ArrayList<>();
    this.postRepository.findBySearch(keyword, keyword, page).forEach(result -> {
      PostDto.SearchResponse response = PostMapper.INSTANCE.toSearchResponseDto(result);
      response.setHashtags(getPostHashtags(result.getPostHashtags()));
      responses.add(response);
    });
    return responses;
  }

  public List<ScheduleDto.Response> getSchedulesResponse(Integer postId) {
    List<ScheduleDto.Response> responses = new ArrayList<>();
    scheduleRepository.findByPostId(postId).forEach(schedule -> {
      ScheduleDto.Response response = PostMapper.INSTANCE.toScheduleResponses(schedule);
      responses.add(response);
    });

    return responses;
  }

  public List<PostDto.ReviewResponse> getReviewsResponse(Integer postId) {
    List<PostDto.ReviewResponse> responses = new ArrayList<>();
    reviewRepository.findByPostId(postId).forEach(review -> {
      User user = review.getUser();
      UserInfo userInfo = user.getUserInfo();
      PostDto.ReviewResponse response =
          PostMapper.INSTANCE.toReviewResponse(review, user, userInfo);
      responses.add(response);
    });
    return responses;
  }

  public List<HashtagDto.Response> getHashtagsResponse(Integer postId) {
    List<HashtagDto.Response> responses = new ArrayList<>();
    postHashtagRepository.findByPostId(postId).forEach(postHashtag -> {
      Hashtag hashTag = postHashtag.getHashTag();
      HashtagDto.Response response = HashtagMapper.INSTANCE.toPostResponseDto(postHashtag, hashTag);
      responses.add(response);
    });
    return responses;
  }
}