package grooteogi.controller;

import grooteogi.dto.ProfileDto;
import grooteogi.dto.UserDto;
import grooteogi.response.BasicResponse;
import grooteogi.service.UserService;
import grooteogi.utils.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserController {

  private final UserService userService;

  @GetMapping("/profile")
  public ResponseEntity<BasicResponse> getUserProfile() {
    Session session = (Session) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    ProfileDto.Response response = userService.getUserProfile(session.getId());

    return ResponseEntity.ok(BasicResponse.builder().data(response).build());
  }

  @PatchMapping("/profile")
  public ResponseEntity<BasicResponse> modifyUserProfile(@RequestBody ProfileDto.Request request) {
    Session session = (Session) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    userService.modifyUserProfile(session.getId(), request);
    return ResponseEntity.ok(BasicResponse.builder().message("modify user info success").build());
  }

  @PatchMapping("/password")
  public ResponseEntity<BasicResponse> modifyUserPw(@RequestBody UserDto.PasswordRequest request) {
    Session session = (Session) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    userService.modifyUserPw(session.getId(), request);
    return ResponseEntity.ok(
        BasicResponse.builder().message("modify user password success").build());
  }
}
