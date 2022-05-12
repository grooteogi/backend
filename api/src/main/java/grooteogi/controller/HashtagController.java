package grooteogi.controller;

import grooteogi.domain.Hashtag;
import grooteogi.dto.hashtag.HashtagDto;
import grooteogi.response.BasicResponse;
import grooteogi.service.HashtagService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hashtag")
@RequiredArgsConstructor
public class HashtagController {

  private final HashtagService hashtagService;

  @GetMapping
  public ResponseEntity<BasicResponse> getHashtag() {
    List<HashtagDto.Response> hashtags = hashtagService.getHashtag();
    return ResponseEntity.ok(BasicResponse.builder().data(hashtags).build());
  }

  @GetMapping("/search")
  public ResponseEntity<BasicResponse> search(
      @RequestParam(name = "keyword", required = false) String keyword) {
    List<Hashtag> hashtag = hashtagService.search(keyword);
    return ResponseEntity.ok(BasicResponse.builder().data(hashtag).build());
  }

  @PostMapping
  public ResponseEntity<BasicResponse> createHashtag(@RequestBody HashtagDto.Request request) {
    HashtagDto.Response createdHashtag = hashtagService.createHashtag(request);
    return ResponseEntity.ok(BasicResponse.builder().data(createdHashtag).build());
  }
}
