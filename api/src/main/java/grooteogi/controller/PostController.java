package grooteogi.controller;

import grooteogi.domain.Post;
import grooteogi.dto.ModifyingPostDto;
import grooteogi.dto.PostDto;
import grooteogi.dto.response.BasicResponse;
import grooteogi.service.PostService;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
  public ResponseEntity<BasicResponse> getAllPost() {
    List<Post> postList = postService.getAllPost();
    return ResponseEntity.ok(
        BasicResponse.builder().count(postList.size()).data(postList).build());
  }

  @GetMapping("/{postId}")
  public ResponseEntity<BasicResponse> getPostDetail(@PathVariable int postId) {
    Post postDetail = postService.getPostDetail(postId);
    return ResponseEntity.ok(
        BasicResponse.builder().data(postDetail).build());
  }


  @PostMapping
  public ResponseEntity<BasicResponse> createPost(@RequestBody PostDto postDto) {
    Post createdPost = this.postService.createPost(postDto);
    return ResponseEntity.ok(BasicResponse.builder().data(createdPost).build());
  }

  @PutMapping
  public ResponseEntity<BasicResponse> modifyPost(@RequestBody ModifyingPostDto modifyingPostDto) {
    Post modifiedPost = this.postService.modifyPost(modifyingPostDto);
    return ResponseEntity.ok(BasicResponse.builder().data(modifiedPost).build());
  }

  @DeleteMapping
  public ResponseEntity<BasicResponse> deletePost(@RequestParam int postId) {
    List<Post> deletePost = this.postService.deletePost(postId);
    return ResponseEntity.ok(BasicResponse.builder().count(
        deletePost.size()).data(deletePost).build());
  }




}
