package grooteogi.controller;

import grooteogi.dto.HashtagDto;
import grooteogi.dto.PostDto;
import grooteogi.dto.PostDto.SearchResponse;
import grooteogi.dto.ScheduleDto;
import grooteogi.response.BasicResponse;
import grooteogi.service.PostService;
import grooteogi.utils.Session;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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

  @GetMapping("/search")
  public ResponseEntity<BasicResponse> search(
      @RequestParam(name = "keyword", required = false) String keyword,
      @RequestParam(name = "page", defaultValue = "1") Integer page,
      @RequestParam(name = "filter", required = false) String filter) {

    List<PostDto.Response> posts = postService.search(keyword, filter,
        PageRequest.of(page - 1, 12));
    return ResponseEntity.ok(
        BasicResponse.builder().message("search post success").data(posts).build());
  }

  @GetMapping("/{postId}")
  public ResponseEntity<BasicResponse> getPostResponse(@PathVariable int postId) {
    PostDto.Response post = postService.getPostResponse(postId);
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
    List<PostDto.ReviewResponse> reviewResponses = postService.getReviewsResponse(postId);
    return ResponseEntity.ok(
        BasicResponse.builder().message("get reviews success").data(reviewResponses).build());
  }

  @GetMapping("/{postId}/like")
  public ResponseEntity<BasicResponse> createHeart(@PathVariable Integer postId) {
    Session session = (Session) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();

    postService.modifyHeart(postId, session.getId());
    return ResponseEntity.ok(
        BasicResponse.builder().message("heart success").build());
  }

  @GetMapping("/{postId}/hashtags")
  public ResponseEntity<BasicResponse> getHashtagsResponse(@PathVariable int postId) {
    List<HashtagDto.Response> hashtagsResponse = postService.getHashtagsResponse(postId);
    return ResponseEntity.ok(
        BasicResponse.builder().message("get postHashtags success").data(hashtagsResponse).build());
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

    PostDto.Response modifiedPost = postService.modifyPost(request, postId, session.getId());
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
}
