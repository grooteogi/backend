package grooteogi.controller;

import grooteogi.domain.User;
import grooteogi.dto.ProfileDto;
import grooteogi.dto.UserDto.Password;
import grooteogi.response.BasicResponse;
import grooteogi.service.UserService;
import grooteogi.utils.Session;
import java.util.List;
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

  @GetMapping
  public ResponseEntity<BasicResponse> getAllUser() {
    List<User> userList = userService.getAllUser();
    return ResponseEntity.ok(BasicResponse.builder().count(userList.size()).data(userList).build());
  }

  @GetMapping("/self")
  public ResponseEntity<BasicResponse> getUser() {
    Session session = (Session) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    User user = userService.getUser(session.getId());

    return ResponseEntity.ok(BasicResponse.builder().data(user).build());
  }

  @GetMapping("/profile")
  public ResponseEntity<BasicResponse> getUserProfile() {
    Session session = (Session) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    User user = userService.getUserProfile(session.getId());

    return ResponseEntity.ok(BasicResponse.builder().data(user).build());
  }

  @PatchMapping("/profile")
  public ResponseEntity<BasicResponse> modifyUserProfile(@RequestBody ProfileDto.Request request) {
    Session session = (Session) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    User user = userService.modifyUserProfile(session.getId(), request);
    return ResponseEntity.ok(BasicResponse.builder().data(user).build());
  }

  @PatchMapping("/password")
  public ResponseEntity<BasicResponse> modifyUserPw(@RequestBody Password request) {
    Session session = (Session) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    userService.modifyUserPw(session.getId(), request);
    return ResponseEntity.ok(
        BasicResponse.builder().message("modify user password success").build());
  }
}
