package grooteogi.controller;

import grooteogi.domain.User;
import grooteogi.dto.user.ProfileDto;
import grooteogi.dto.user.PwDto;
import grooteogi.dto.user.SessionDto;
import grooteogi.response.BasicResponse;
import grooteogi.service.UserService;
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
    SessionDto sessionDto = (SessionDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userService.getUser(sessionDto.getID());

    return ResponseEntity.ok(BasicResponse.builder().data(user).build());
  }

  @GetMapping("/profile")
  public ResponseEntity<BasicResponse> getUserProfile() {
    SessionDto sessionDto = (SessionDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userService.getUserProfile(sessionDto.getID());

    return ResponseEntity.ok(BasicResponse.builder().data(user).build());
  }

  @PatchMapping("/profile")
  public ResponseEntity<BasicResponse> modifyUserProfile(@RequestBody ProfileDto profileDto) {
    SessionDto sessionDto = (SessionDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userService.modifyUserProfile(sessionDto.getID(), profileDto);
    return ResponseEntity.ok(BasicResponse.builder().data(user).build());
  }

  @PatchMapping("/password")
  public ResponseEntity<BasicResponse> modifyUserPw(@RequestBody PwDto pwDto) {
    SessionDto sessionDto = (SessionDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    userService.modifyUserPw(sessionDto.getID(), pwDto);
    return ResponseEntity.ok(
        BasicResponse.builder().message("modify user password success").build());
  }
}
