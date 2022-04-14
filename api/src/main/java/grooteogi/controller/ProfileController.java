package grooteogi.controller;

import grooteogi.domain.User;
import grooteogi.dto.ProfileDto;
import grooteogi.dto.response.BasicResponse;
import grooteogi.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/profile")
@RestController
public class ProfileController {

  private final ProfileService profileService;

  @GetMapping("/user/{userId}")
  public ResponseEntity<BasicResponse> getUserInfo(@PathVariable Integer userId) {
    User user = profileService.getUserInfo(userId);

    return ResponseEntity.ok(BasicResponse.builder().data(user).build());
  }

  @PatchMapping("/user/{userId}")
  public ResponseEntity<BasicResponse> modifyUserInfo(@PathVariable Integer userId,
      @RequestBody ProfileDto profileDto) {

    User user = profileService.modifyUserInfo(userId, profileDto);
    return ResponseEntity.ok(BasicResponse.builder().data(user).build());
  }
}
