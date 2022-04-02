package grooteogi.controller;

import grooteogi.domain.Hashtag;
import grooteogi.domain.HashtagType;
import grooteogi.service.HashtagService;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HashtagController {
  private final HashtagService hashtagService;


  @GetMapping("/hashtag")
  public ResponseEntity<List<Hashtag>> getAllHashtag() {
    return ResponseEntity.ok(this.hashtagService.getAllHashtag());
  }

  @GetMapping("/hashtag/top10")
  public ResponseEntity<List<Hashtag>> getTopTenHashtag(@RequestParam String type) {
    return ResponseEntity.ok(this.hashtagService.getTopTenHashtag(type));
  }

  @PostMapping("/hashtag")
  public ResponseEntity<Hashtag> createHashtag(@RequestParam String tag) {
    Hashtag createdHashtag = this.hashtagService.createHashtag(tag);
    return ResponseEntity.ok(createdHashtag);
  }
}
