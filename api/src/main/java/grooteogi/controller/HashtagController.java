package grooteogi.controller;

import grooteogi.domain.Hashtag;
import grooteogi.dto.hashtag.HashtagDto;
import grooteogi.response.BasicResponse;
import grooteogi.service.HashtagService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
  public ResponseEntity<BasicResponse> search(
      @RequestParam(name = "page", defaultValue = "1") Integer page,
      @RequestParam(name = "type", required = false) String type,
      @RequestParam(name = "size", defaultValue = "20") Integer size) {

    List<Hashtag> hashtags = hashtagService.search(PageRequest.of(page - 1, size), type);
    return ResponseEntity.ok(BasicResponse.builder().data(hashtags).build());
  }

  @PostMapping
  public ResponseEntity<BasicResponse> createHashtag(@RequestBody HashtagDto hashtagDto) {
    Hashtag createdHashtag = this.hashtagService.createHashtag(hashtagDto);
    return ResponseEntity.ok(BasicResponse.builder().data(createdHashtag).build());
  }
}
