package grooteogi.controller;

import grooteogi.domain.Post;
import grooteogi.dto.PostDto;
import grooteogi.response.BasicResponse;
import grooteogi.service.PostService;
import grooteogi.response.CursorResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
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

  @GetMapping
  public ResponseEntity<BasicResponse> search(
      @RequestParam(name = "search", required = false) String search,
      @RequestParam(name = "cursor", required = false) Integer cursor,
      @RequestParam(name = "type", required = false) String type) {
    if (cursor == null) {
      cursor = 0;
    }
    CursorResult<Post> posts = postService.search(search, cursor, type, PageRequest.of(0, 20));
    return ResponseEntity.ok(BasicResponse.builder().data(posts).build());
  }

  @GetMapping("/{postId}")
  public ResponseEntity<BasicResponse> getPost(@PathVariable int postId) {
    Post post = postService.getPost(postId);
    return ResponseEntity.ok(BasicResponse.builder().data(post).build());
  }

  @PostMapping
  public ResponseEntity<BasicResponse> createPost(@RequestBody PostDto postDto) {
    Post createdPost = this.postService.createPost(postDto);
    return ResponseEntity.ok(BasicResponse.builder().data(createdPost).build());
  }

  @PutMapping("/{postId}")
  public ResponseEntity<BasicResponse> modifyPost(@RequestBody PostDto modifiedDto,
      @PathVariable int postId) {
    Post modifiedPost = this.postService.modifyPost(modifiedDto, postId);
    return ResponseEntity.ok(BasicResponse.builder().data(modifiedPost).build());
  }

  @DeleteMapping("/{postId}")
  public ResponseEntity<BasicResponse> deletePost(@PathVariable int postId) {
    List<Post> deletePost = this.postService.deletePost(postId);
    return ResponseEntity.ok(
        BasicResponse.builder().count(deletePost.size()).data(deletePost).build());
  }
}
