package grooteogi.controller;

import static org.yaml.snakeyaml.util.UriEncoder.decode;

import grooteogi.dto.HashtagDto;
import grooteogi.dto.LikeDto;
import grooteogi.dto.PostDto;
import grooteogi.dto.ReviewDto;
import grooteogi.dto.ScheduleDto;
import grooteogi.response.BasicResponse;
import grooteogi.service.PostService;
import grooteogi.utils.JwtProvider;
import grooteogi.utils.Session;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

  private final PostService postService;
  private final JwtProvider jwtProvider;

  @GetMapping("/search")
  public ResponseEntity<BasicResponse> search(
      @RequestParam(name = "keyword", required = false) String keyword,
      @RequestParam(name = "page", defaultValue = "1") Integer page,
      @RequestParam(name = "filter", required = false) String filter,
      @RequestParam(name = "region") String region) {

    PostDto.SearchResponse searchResponse = postService.search(decode(keyword), filter,
        PageRequest.of(page - 1, 12, Sort.by("id").descending()), decode(region));
    return ResponseEntity.ok(
        BasicResponse.builder().message("search post success").data(searchResponse).build());
  }

  @GetMapping("/detail/{postId}")
  public ResponseEntity<BasicResponse> getPostResponse(
      HttpServletRequest request, @PathVariable int postId) {
    PostDto.Response post = postService.getPostResponse(
        postId, request.getHeader(HttpHeaders.AUTHORIZATION));
    return ResponseEntity.ok(
        BasicResponse.builder().message("get post success").data(post).build());
  }

  @GetMapping("/{postId}/schedules")
  public ResponseEntity<BasicResponse> getSchedulesResponse(@PathVariable int postId) {
    List<ScheduleDto.Response> schedulesResponse = postService.getSchedulesResponse(postId);
    return ResponseEntity.ok(
        BasicResponse.builder().message("get schedules success").data(schedulesResponse).build());
  }

  @GetMapping("/{postId}/reviews")
  public ResponseEntity<BasicResponse> getReviewsResponse(@PathVariable int postId) {
    List<ReviewDto.Response> reviewResponses = postService.getReviewsResponse(postId);
    return ResponseEntity.ok(
        BasicResponse.builder().message("get reviews success").data(reviewResponses).build());
  }

  @GetMapping("/{postId}/like")
  public ResponseEntity<BasicResponse> createHeart(@PathVariable Integer postId) {
    Session session = (Session) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();

    LikeDto.Response response = postService.modifyHeart(postId, session.getId());
    return ResponseEntity.ok(
        BasicResponse.builder().message("heart success").data(response).build());
  }

  @GetMapping("/{postId}/hashtags")
  public ResponseEntity<BasicResponse> getHashtagsResponse(@PathVariable int postId) {
    List<HashtagDto.Response> hashtagsResponse = postService.getHashtagsResponse(postId);
    return ResponseEntity.ok(
        BasicResponse.builder().message("get postHashtags success").data(hashtagsResponse).build());
  }

  @GetMapping("/likes")
  public ResponseEntity<BasicResponse> getLikePosts() {
    Session session = (Session) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();

    List<PostDto.SearchResult> postResponse = postService.getLikePosts(session.getId());

    return ResponseEntity.ok(
        BasicResponse.builder().message("get likePosts success").data(postResponse).build());
  }

  @PostMapping
  public ResponseEntity<BasicResponse> createPost(@RequestBody PostDto.Request request) {
    Session session = (Session) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();

    PostDto.CreateResponse createdPost = postService.createPost(request, session.getId());
    return ResponseEntity.ok(
        BasicResponse.builder().message("create post success").data(createdPost).build());
  }

  @PutMapping("/{postId}")
  public ResponseEntity<BasicResponse> modifyPost(@RequestBody PostDto.Request request,
      @PathVariable int postId) {
    Session session = (Session) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();

    PostDto.CreateResponse modifiedPost = postService.modifyPost(request, postId, session.getId());
    return ResponseEntity.ok(
        BasicResponse.builder().message("modify post success").data(modifiedPost).build());
  }

  @DeleteMapping("/{postId}")
  public ResponseEntity<BasicResponse> deletePost(@PathVariable int postId) {
    Session session = (Session) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();

    postService.deletePost(postId, session.getId());
    return ResponseEntity.ok(BasicResponse.builder().message("delete post success").build());
  }

  @GetMapping("/writer")
  public ResponseEntity<BasicResponse> writerPost() {
    Session session = (Session) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();

    List<PostDto.SearchResult> responses = postService.writerPost(session.getId());
    return ResponseEntity.ok(BasicResponse.builder()
        .message("get writer post success").data(responses).build());
  }
}
