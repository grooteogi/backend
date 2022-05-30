package grooteogi.service;

import grooteogi.domain.Hashtag;
import grooteogi.domain.Heart;
import grooteogi.domain.Post;
import grooteogi.domain.PostHashtag;
import grooteogi.domain.Reservation;
import grooteogi.domain.Schedule;
import grooteogi.domain.User;
import grooteogi.domain.UserInfo;
import grooteogi.dto.HashtagDto;
import grooteogi.dto.LikeDto;
import grooteogi.dto.PostDto;
import grooteogi.dto.PostDto.SearchResult;
import grooteogi.dto.ReviewDto;
import grooteogi.dto.ScheduleDto;
import grooteogi.enums.PostFilterEnum;
import grooteogi.enums.RegionType;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.mapper.HashtagMapper;
import grooteogi.mapper.PostMapper;
import grooteogi.mapper.ReviewMapper;
import grooteogi.repository.HashtagRepository;
import grooteogi.repository.HeartRepository;
import grooteogi.repository.PostHashtagRepository;
import grooteogi.repository.PostRepository;
import grooteogi.repository.ReservationRepository;
import grooteogi.repository.ReviewRepository;
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
import org.springframework.data.domain.Page;
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
  private final HeartRepository heartRepository;

  public PostDto.Response getPostResponse(Integer postId, Integer currentUser) {
    Optional<Post> post = postRepository.findById(postId);
    if (post.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.POST_NOT_FOUND_EXCEPTION);
    }

    //조회수 증가
    post.get().setViews(post.get().getViews() + 1);
    Post updatePost = postRepository.save(post.get());

    PostDto.Response result = PostMapper.INSTANCE.toDetailResponse(updatePost);

    User user = post.get().getUser();
    result.setMentor(PostMapper.INSTANCE.toUserResponse(user, user.getUserInfo()));

    List<Heart> hearts = heartRepository.findByPost(post.get());
    hearts.forEach(heart -> {
      int heartUser = heart.getUser().getId();
      LikeDto.Response likes = LikeDto.Response.builder()
          .liked(heartUser == currentUser).count(hearts.size()).build();

      result.setLikes(likes);
    });

    if (hearts.isEmpty()) {
      LikeDto.Response likeRespones = LikeDto.Response.builder().liked(false).count(0).build();
      result.setLikes(likeRespones);
    }

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

  public List<PostDto.SearchResult> getLikePosts(Integer userId) {
    List<Heart> hearts = heartRepository.findByUserId(userId);
    List<PostDto.SearchResult> response = new ArrayList<>();

    if (hearts.size() > 0) {
      hearts.forEach(heart -> {
        PostDto.SearchResult result = PostMapper.INSTANCE.toSearchResponseDto(heart.getPost());
        response.add(result);
      });
    }

    return response;
  }

  private List<PostHashtag> createPostHashtag(String[] postHashtags) {
    List<PostHashtag> postHashtagList = new ArrayList<>();
    Arrays.stream(postHashtags).forEach(name -> {
      Optional<Hashtag> hashtag = hashtagRepository.findByName(name);
      hashtag.ifPresent(tag -> {
        tag.setCount(tag.getCount() + 1);
        PostHashtag createdPostHashtag = PostHashtag.builder().hashTag(tag).build();
        postHashtagRepository.save(createdPostHashtag);
        postHashtagList.add(createdPostHashtag);
      });
    });
    return postHashtagList;
  }

  public PostDto.CreateResponse createPost(PostDto.Request request, Integer userId) {

    List<Schedule> schedules = createSchedule(request.getSchedules());

    List<PostHashtag> postHashtags = createPostHashtag(request.getHashtags());

    Optional<User> user = userRepository.findById(userId);

    user.orElseThrow(() -> new ApiException(ApiExceptionEnum.USER_NOT_FOUND_EXCEPTION));

    Post createdPost = PostMapper.INSTANCE.toEntity(request, user.get(), schedules, postHashtags);

    Post savedPost = postRepository.save(createdPost);

    schedules.forEach(schedule -> {
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
  public PostDto.CreateResponse modifyPost(PostDto.Request request,
      Integer postId, Integer userId) {
    Optional<Post> post = postRepository.findById(postId);
    int writer = post.get().getUser().getId();

    if (userId != writer) {
      throw new ApiException(ApiExceptionEnum.NO_PERMISSION_EXCEPTION);
    }

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
    return PostMapper.INSTANCE.toCreateResponseDto(modifiedPost);
  }

  public void deletePost(Integer postId, Integer userId) {
    Optional<Post> post = postRepository.findById(postId);

    int writer = post.get().getUser().getId();

    if (userId != writer) {
      throw new ApiException(ApiExceptionEnum.NO_PERMISSION_EXCEPTION);
    }

    if (post.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.POST_NOT_FOUND_EXCEPTION);
    }

    List<Reservation> reservations = reservationRepository.findByPostId(post.get().getId());
    reservationRepository.deleteAll(reservations);

    this.postRepository.delete(post.get());
  }

  public PostDto.SearchResponse search(String keyword, String filter,
      Pageable page, String region) {

    return keyword == null ? searchAllPosts(page, filter, region)
        : searchPosts(keyword, page, filter, region);

  }

  private PostDto.SearchResponse filter(List<Post> postList, String filter, int pageCount) {

    PostFilterEnum postFilterEnum = PostFilterEnum.valueOf(filter);

    List<SearchResult> searchResults = new ArrayList<>();

    if (postFilterEnum == PostFilterEnum.VIEWS) {
      List<Post> filteredPostList = postList.stream()
          .sorted(Comparator.comparingInt(Post::getViews).reversed())
          .collect(Collectors.toList());

      filteredPostList.forEach(
          post -> searchResults.add(PostMapper.INSTANCE.toSearchResponseDto(post)));


    } else if (postFilterEnum == PostFilterEnum.POPULAR) {
      List<Post> filteredPostList = postList.stream()
          .filter(post -> post.getHearts().size() > 0)
          .sorted(Comparator.comparingInt(post -> post.getHearts().size()))
          .collect(Collectors.toList());
      Collections.reverse(filteredPostList);

      filteredPostList.forEach(
          post -> searchResults.add(PostMapper.INSTANCE.toSearchResponseDto(post)));


    } else {

      postList.forEach(post -> searchResults.add(PostMapper.INSTANCE.toSearchResponseDto(post)));
    }

    return PostDto.SearchResponse.builder().posts(searchResults).pageCount(pageCount).build();
  }

  private List<Schedule> filterRegion(List<Schedule> schedules, String region) {
    RegionType regionType = RegionType.getEnum(region);

    return schedules.stream().filter(schedule -> schedule.getRegion() == regionType)
        .collect(Collectors.toList());
  }

  public PostDto.SearchResponse searchAllPosts(Pageable page, String filter, String region) {
    Page<Post> posts = postRepository.findAll(page);
    posts.forEach(
        post -> filterRegion(post.getSchedules(), region)
    );

    return filter(posts.getContent(), filter, posts.getTotalPages());
  }

  private PostDto.SearchResponse searchPosts(String keyword, Pageable page,
      String filter, String region) {
    Page<Post> posts =
        postRepository.findAllByTitleContainingOrContentContaining(keyword, keyword, page);

    posts.forEach(
        post -> filterRegion(post.getSchedules(), region)
    );
    return filter(posts.getContent(), filter, posts.getTotalPages());
  }

  public List<ScheduleDto.Response> getSchedulesResponse(Integer postId) {
    List<ScheduleDto.Response> responses = new ArrayList<>();
    scheduleRepository.findByPostId(postId).forEach(schedule -> {
      ScheduleDto.Response response = PostMapper.INSTANCE.toScheduleResponses(schedule);
      responses.add(response);
    });

    return responses;
  }

  public List<ReviewDto.Response> getReviewsResponse(Integer postId) {
    List<ReviewDto.Response> responses = new ArrayList<>();
    reviewRepository.findByPostId(postId).forEach(review -> {
      User user = review.getUser();
      UserInfo userInfo = user.getUserInfo();
      ReviewDto.Response response = ReviewMapper.INSTANCE.toReviewResponse(review, user,
          userInfo);
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

  public void modifyHeart(Integer postId, Integer userId) {
    Optional<Post> post = postRepository.findById(postId);
    if (post.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.POST_NOT_FOUND_EXCEPTION);
    }

    Optional<User> user = userRepository.findById(userId);
    Optional<Heart> heart = heartRepository.findByPostIdUserId(postId, userId);

    if (heart.isEmpty()) {
      heartRepository.save(Heart.builder().post(post.get()).user(user.get()).build());
    } else {
      heartRepository.delete(heart.get());
    }
  }

  public List<PostDto.SearchResult> writerPost(Integer userId) {

    List<Post> posts = postRepository.findByUserId(userId);
    List<PostDto.SearchResult> responses = new ArrayList<>();

    if (!posts.isEmpty()) {
      posts.forEach(post -> {
        PostDto.SearchResult response = PostMapper.INSTANCE.toSearchResponseDto(post);
        responses.add(response);
      });
    }
    return responses;
  }


}
