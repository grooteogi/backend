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
import grooteogi.utils.JwtProvider;
import grooteogi.utils.Session;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
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

  private final JwtProvider jwtProvider;

  public PostDto.Response getPostResponse(Integer postId, String jwt) {
    Optional<Post> post = postRepository.findById(postId);
    if (post.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.POST_NOT_FOUND_EXCEPTION);
    }

    //조회수 증가
    post.get().setViews(post.get().getViews() + 1);
    Post updatePost = postRepository.save(post.get());

    PostDto.Response result = PostMapper.INSTANCE.toDetailResponse(updatePost);

    result.setHashtags(getPostHashtags(updatePost.getPostHashtags()));

    User user = post.get().getUser();
    result.setMentor(PostMapper.INSTANCE.toUserResponse(user, user.getUserInfo()));
    result.setIsAuthor(false);

    List<Heart> hearts = heartRepository.findByPost(post.get());

    if (jwt != null) {
      jwt = jwtProvider.extractToken(jwt);
      jwtProvider.isUsable(jwt);
      Session session = jwtProvider.extractAllClaims(jwt);

      if (result.getMentor().getUserId() == (session.getId())) {
        result.setIsAuthor(true);
      }

      Optional<Heart> heart =
          heartRepository.findByPostIdUserId(post.get().getId(), session.getId());
      LikeDto.Response likeResponse =
          LikeDto.Response.builder().liked(!heart.isEmpty()).count(hearts.size()).build();
      result.setLikes(likeResponse);
    } else {
      LikeDto.Response likeResponse =
          LikeDto.Response.builder().liked(false).count(hearts.size()).build();
      result.setLikes(likeResponse);
    }

    return result;
  }

  private List<Schedule> createSchedule(List<ScheduleDto.Request> requests) {
    List<Schedule> schedules = PostMapper.INSTANCE.toScheduleEntities(requests);

    scheduleRepository.saveAll(schedules);
    return schedules;
  }

  private String[] getPostHashtags(List<PostHashtag> postHashtags) {
    List<String> response = new ArrayList<>();
    postHashtags.forEach(postHashtag -> response.add(postHashtag.getHashTag().getName()));
    return response.toArray(new String[0]);
  }

  public List<PostDto.SearchResult> getLikePosts(Integer userId) {
    List<Heart> hearts = heartRepository.findByUserId(userId);
    List<PostDto.SearchResult> response = new ArrayList<>();

    if (hearts.size() > 0) {
      hearts.forEach(heart -> {
        PostDto.SearchResult result = PostMapper.INSTANCE.toSearchResponseDto(heart.getPost());
        result.setHashtags(getPostHashtags(heart.getPost().getPostHashtags()));
        response.add(result);
      });
    }

    return response;
  }

  private List<PostHashtag> createPostHashtag(String[] postHashtags) {
    List<PostHashtag> postHashtagList = new ArrayList<>();
    Arrays.stream(postHashtags).forEach(name -> {
      Optional<Hashtag> hashtag = hashtagRepository.findByName(name);
      if (hashtag.isEmpty()) {
        Hashtag createdHashtag = Hashtag.builder().name(name).build();
        hashtagRepository.save(createdHashtag);
        hashtag = hashtagRepository.findByName(name);
      }
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

      if (hashtag.isEmpty()) {
        Hashtag createdHashtag = Hashtag.builder().name(name).build();
        hashtagRepository.save(createdHashtag);
        hashtag = Optional.of(createdHashtag);
      }

      hashtag.ifPresent(tag -> {
        tag.setCount(tag.getCount() + 1);
        modifiedPostHashtag.setHashTag(tag);
        modifiedPostHashtag.setPost(post.get());

        postHashtagRepository.save(modifiedPostHashtag);
      });
    });

    List<Reservation> reservations = reservationRepository.findByPostId(post.get().getId());
    List<Schedule> reservedSchedule = new ArrayList<>();
    reservations.forEach(reservation -> reservedSchedule.add(reservation.getSchedule()));

    post.get().getSchedules().stream()
        .filter(filter -> reservedSchedule.stream()
            .noneMatch(Predicate.isEqual(filter)))
        .collect(Collectors.toList())
        .forEach(result -> scheduleRepository.delete(result));

    List<Schedule> modifySchedule = createSchedule(request.getSchedules());
    modifySchedule.forEach(schedule -> {
      schedule.setPost(post.get());
      scheduleRepository.save(schedule);
    });

    Post modifiedPost = PostMapper.INSTANCE.toModify(post.get(), request, modifySchedule);
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

    RegionType regionType = RegionType.getEnum(region);

    return keyword == null ? searchAllPosts(page, filter, regionType)
        : searchPosts(keyword, page, filter, regionType);

  }

  private PostDto.SearchResponse filter(List<Post> postList, String filter, int pageCount) {

    PostFilterEnum postFilterEnum = PostFilterEnum.valueOf(filter);

    List<SearchResult> searchResults = new ArrayList<>();

    if (postFilterEnum == PostFilterEnum.VIEWS) {
      List<Post> filteredPostList = postList.stream()
          .sorted(Comparator.comparingInt(Post::getViews).reversed())
          .collect(Collectors.toList());

      filteredPostList.forEach(
          post -> {
            PostDto.SearchResult result = PostMapper.INSTANCE.toSearchResponseDto(post);
            result.setHashtags(getPostHashtags(post.getPostHashtags()));
            searchResults.add(result);
          });


    } else if (postFilterEnum == PostFilterEnum.POPULAR) {
      List<Post> filteredPostList = postList.stream()
          .filter(post -> post.getHearts().size() > 0)
          .sorted(Comparator.comparingInt(post -> post.getHearts().size()))
          .collect(Collectors.toList());
      Collections.reverse(filteredPostList);

      filteredPostList.forEach(post -> {
        PostDto.SearchResult result = PostMapper.INSTANCE.toSearchResponseDto(post);
        result.setHashtags(getPostHashtags(post.getPostHashtags()));
        searchResults.add(result);
      });


    } else {

      postList.forEach(post -> {
        PostDto.SearchResult result = PostMapper.INSTANCE.toSearchResponseDto(post);
        result.setHashtags(getPostHashtags(post.getPostHashtags()));
        searchResults.add(result);
      });
    }

    return PostDto.SearchResponse.builder().posts(searchResults).pageCount(pageCount).build();
  }


  public PostDto.SearchResponse searchAllPosts(Pageable page, String filter, RegionType region) {
    Page<Post> posts = postRepository.findAll(page);

    List<Post> postList = new ArrayList<>();

    posts.forEach(
        post -> post.getSchedules().stream()
            .filter(s -> s.getRegion() == region).collect(
                Collectors.toList()).forEach(schedule -> {
                  if (!postList.contains(schedule.getPost())) {
                    postList.add(schedule.getPost());
                  }
                })
    );

    return filter(postList, filter, posts.getTotalPages());
  }

  private PostDto.SearchResponse searchPosts(String keyword, Pageable page,
      String filter, RegionType region) {
    Page<Post> posts =
        postRepository.findAllByTitleContainingOrContentContaining(keyword, keyword, page);

    List<Post> postList = new ArrayList<>();

    posts.forEach(
        post -> post.getSchedules().stream()
            .filter(s -> s.getRegion() == region).collect(
                Collectors.toList()).forEach(schedule -> {
                  if (!postList.contains(schedule.getPost())) {
                    postList.add(schedule.getPost());
                  }
                })
    );

    return filter(postList, filter, posts.getTotalPages());
  }

  public List<ScheduleDto.Response> getSchedulesResponse(Integer postId) {
    List<Reservation> reservations = reservationRepository.findByPostId(postId);
    List<Schedule> reservedSchedule = new ArrayList<>();
    reservations.forEach(reservation -> reservedSchedule.add(reservation.getSchedule()));

    List<Schedule> postSchedules = scheduleRepository.findByPostId(postId);
    List<ScheduleDto.Response> responses = new ArrayList<>();

    postSchedules.stream()
        .filter(filter -> reservedSchedule.stream()
            .noneMatch(Predicate.isEqual(filter)))
        .forEach(schedule -> {
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

  public LikeDto.Response modifyHeart(Integer postId, Integer userId) {
    Optional<Post> post = postRepository.findById(postId);

    if (post.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.POST_NOT_FOUND_EXCEPTION);
    }

    Optional<User> user = userRepository.findById(userId);
    Optional<Heart> heart = heartRepository.findByPostIdUserId(postId, userId);

    if (heart.isEmpty()) {
      heartRepository.save(Heart.builder().post(post.get()).user(user.get()).build());
      return LikeDto.Response.builder().liked(true)
          .count(post.get().getHearts().size()).build();
    } else {
      heartRepository.delete(heart.get());
      return LikeDto.Response.builder().liked(false)
          .count(post.get().getHearts().size()).build();
    }

  }

  public List<PostDto.SearchResult> writerPost(Integer userId) {

    List<Post> posts = postRepository.findByUserId(userId);
    List<PostDto.SearchResult> responses = new ArrayList<>();

    if (!posts.isEmpty()) {
      posts.forEach(post -> {
        PostDto.SearchResult response = PostMapper.INSTANCE.toSearchResponseDto(post);
        response.setHashtags(getPostHashtags(post.getPostHashtags()));
        responses.add(response);
      });
    }
    return responses;
  }


}