package grooteogi.controller;

import grooteogi.domain.Hashtag;
import grooteogi.dto.response.BasicResponse;
import grooteogi.service.HashtagService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hashtag")
@RequiredArgsConstructor
public class HashtagController {

  private final HashtagService hashtagService;

  @GetMapping("/")
  public ResponseEntity<BasicResponse> getAllHashtag() {
    List<Hashtag> hashtagList = hashtagService.getAllHashtag();
    return ResponseEntity.ok(
        BasicResponse.builder().status(HttpStatus.OK.value()).data(hashtagList).build());
  }

  @GetMapping("/top10")
  public ResponseEntity<BasicResponse> getTopTenHashtag(@RequestParam String type) {
    List<Hashtag> hashtagList = hashtagService.getTopTenHashtag(type);
    return ResponseEntity.ok(
        BasicResponse.builder().status(HttpStatus.OK.value()).data(hashtagList).build());
  }

  @PostMapping("/")
  public ResponseEntity<BasicResponse> createHashtag(@RequestParam String tag) {
    Hashtag createdHashtag = this.hashtagService.createHashtag(tag);
    return ResponseEntity.ok(
        BasicResponse.builder().status(HttpStatus.OK.value()).data(createdHashtag).build());
  }
}
