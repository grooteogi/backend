package grooteogi.controller;

import grooteogi.domain.UserHashtag;
import grooteogi.dto.UserHashtagDto;
import grooteogi.response.BasicResponse;
import grooteogi.service.UserHashtagService;
import grooteogi.utils.Session;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserHashtagController {

  private final UserHashtagService userHashtagService;

  @GetMapping("/hashtag")
  public ResponseEntity<BasicResponse> search(
      @RequestParam(name = "page", defaultValue = "1") Integer page) {
    Session session = (Session) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();

    List<UserHashtag> userHashtags = userHashtagService.search(
        session.getId(), PageRequest.of(page - 1, 20));
    return ResponseEntity.ok(BasicResponse.builder().data(userHashtags).build());
  }

  @PostMapping("/hashtag")
  public ResponseEntity<BasicResponse> saveUserHashtag(@RequestBody UserHashtagDto userHashtagDto) {
    Session session = (Session) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    List<UserHashtag> hashtagList = userHashtagService.saveUserHashtag(userHashtagDto,
        session.getId());
    return ResponseEntity.ok(
        BasicResponse.builder().count(hashtagList.size()).data(hashtagList).build());
  }

  @PutMapping("/hashtag")
  public ResponseEntity<BasicResponse> modifyUserHashtag(
      @RequestBody UserHashtagDto userHashtagDto) {
    Session session = (Session) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    List<UserHashtag> hashtagList = userHashtagService.modifyUserHashtag(userHashtagDto,
        session.getId());
    return ResponseEntity.ok(
        BasicResponse.builder().count(hashtagList.size()).data(hashtagList).build());
  }

  @DeleteMapping("/hashtag")
  public ResponseEntity<BasicResponse> deleteUserHashtag(String[] hashtags) {
    Session session = (Session) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    List<UserHashtag> hashtagList = userHashtagService.deleteUserHashtag(session.getId(), hashtags);
    return ResponseEntity.ok(
        BasicResponse.builder().count(hashtagList.size()).data(hashtagList).build());
  }
}
