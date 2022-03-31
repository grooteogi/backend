package grooteogi.controller;

import grooteogi.domain.Hashtag;
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

  @GetMapping("/hashtag/getList")
  public ResponseEntity<List<Hashtag>> getTopByCountHashtag() {
    return ResponseEntity.ok(this.hashtagService.getTopByCountHashtag());
  }

  @PostMapping("/hashtag/create")
  public ResponseEntity<Hashtag> createHashtag(@RequestParam String tag) {
    Hashtag createdHashtag = this.hashtagService.createHashtag(tag);
    return ResponseEntity.ok(createdHashtag);
  }
}
