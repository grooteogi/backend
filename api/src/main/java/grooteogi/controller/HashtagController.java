package grooteogi.controller;

import grooteogi.domain.Hashtag;
import grooteogi.service.HashtagService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HashtagController {
  private final HashtagService hashtagService;

  @Autowired
  public HashtagController(HashtagService hashtagService) {
    this.hashtagService = hashtagService;
  }


  @GetMapping({"/hashtag/getAll"})
  public ResponseEntity<List<Hashtag>> getAllHashtag() {
    return ResponseEntity.ok(this.hashtagService.getAllHashtag());
  }

  @PostMapping({"/hashtag/create"})
  public ResponseEntity<Hashtag> createHashtag(@RequestParam String tag) {
    Hashtag created = this.hashtagService.createHashtag(tag);
    return ResponseEntity.ok(created);
  }
}
