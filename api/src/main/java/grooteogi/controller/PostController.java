package grooteogi.controller;

import grooteogi.domain.Post;
import grooteogi.dto.response.BasicResponse;
import grooteogi.service.PageService;
import grooteogi.utils.CursorResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

  private final PageService pageService;

  @PostMapping("/")
  public ResponseEntity<BasicResponse> search(
      @RequestParam(name = "search", required = false) String search,
      @RequestParam(name = "cursor", required = false) Long cursor) {
    if (cursor == null) {
      cursor = 0L;
    }
    CursorResult<Post> posts =
        pageService.search(search, cursor, PageRequest.of(0, 20));
    return ResponseEntity.ok(BasicResponse.builder().data(posts).build());
  }
}
