package grooteogi.controller;

import grooteogi.dto.*;
import grooteogi.domain.User;
import grooteogi.service.EmailService;
import grooteogi.service.UserService;
import grooteogi.dto.Response;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class UserController {

  private final UserService userService;
  private final EmailService emailService;

  @GetMapping("/user")
  public ResponseEntity<Response> getAllUser() {
    List<User> userList = userService.getAllUser();
    return ResponseEntity.ok(
        Response.builder()
            .status(HttpStatus.OK.value())
            .data(userList).build());
  }

  @GetMapping("/user/{user_id}")
  public ResponseEntity<Response> getUser(@PathVariable Integer user_id){
    User user = userService.getUser(user_id);
    return ResponseEntity.ok(
        Response.builder()
            .status(HttpStatus.OK.value())
            .data(user).build());
  }

  @PatchMapping("/user/{user_id}")
  public ResponseEntity patchUser(@PathVariable Integer user_id){
    return ResponseEntity.ok(null);
  }

  @DeleteMapping("/user/{user_id}")
  public ResponseEntity deleteUser(@PathVariable Integer user_id){
    return ResponseEntity.ok(null);
  }

  /*
  일반 회원가입 중 이메일 인증 버튼 누를 경우 ( 유효성 검사, 이메일 중복 검사 )
  */
  @PostMapping("/user/email-verification/create")
  public ResponseEntity<Response> createEmailVerification(
      @Valid @RequestBody EmailRequest email, BindingResult bindingResult){
    Response response = new Response();

    if(bindingResult.hasErrors()){
      response.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setData(bindingResult.getAllErrors());
      return ResponseEntity.ok(response);
    }

    if(emailService.isExist(email)) {
      response.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setMessage("this email already exists");
    } else {
      emailService.createEmailVerification(email);
      response.setStatus(HttpStatus.OK.value());
      response.setMessage("send email verification success");
    }

    return ResponseEntity.ok(response);
  }

  /*
  일반 회원가입 중 이메일 인증 버튼을 누를 경우 ( 인증 코드 확인 )
  */
  @PostMapping("/user/email-verification/confirm")
  public ResponseEntity<Response> confirmEmailVerification(
      @RequestBody EmailCodeRequest emailCodeRequest) {
    Response response = new Response();

    if (emailService.confirmEmailVerification(emailCodeRequest)) {
      response.setStatus(HttpStatus.OK.value());
      response.setMessage("confirm email verification success");
    } else {
      response.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setMessage("time out");
    }

    return ResponseEntity.ok(response);
  }

  /*
  일반 회원가입 중 가입 버튼을 누를 경우 ( 비밀번호 유효성 검사 )
  TODO 일반 회원가입과 OAuth 회원가입의 dto를 다르게 할지 결정 필요. 다르게 할 시 api도 다르게
   */
  @PostMapping("/user/register")
  public ResponseEntity<Response> register(
      @Valid @RequestBody UserDto userDto, BindingResult bindingResult){
    Response response = new Response();

    if(bindingResult.hasErrors()){
      response.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setData(bindingResult.getAllErrors());
    } else {
      response.setStatus(HttpStatus.OK.value());
      response.setData(userService.register(userDto));
    }

    return ResponseEntity.ok(response);
  }

  @PostMapping("/user/login")
  public ResponseEntity<Response> login(@RequestBody LoginDto loginDto){
    Response response = new Response();
    Token token = userService.login(loginDto);

    if (token == null) {
      response.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setMessage("오류");
    }
    else {
      response.setStatus(HttpStatus.OK.value());
      response.setData(token);
    }

    return ResponseEntity.ok(response);
  }

  @PostMapping("/user/oauth/register")
  public ResponseEntity<Response> oauthRegister(
      @Valid @RequestBody UserDto userDto, BindingResult bindingResult){
    Response response = new Response();

    if(bindingResult.hasErrors()){
      response.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setData(bindingResult.getAllErrors());
    } else {
      response.setStatus(HttpStatus.OK.value());
      response.setData(userService.register(userDto));
    }

    return ResponseEntity.ok(response);
  }

  @PostMapping("/user/oauth/login")
  public ResponseEntity<Response> oauthLogin(@RequestBody LoginDto loginDto){
    Response response = new Response();

    Token token = userService.login(loginDto);

    if (token == null) {
      response.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setMessage("오류");
    }
    else {
      response.setStatus(HttpStatus.OK.value());
      response.setData(token);
    }

    return ResponseEntity.ok(response);
  }

  @GetMapping("/user/verify")
  public ResponseEntity verify(HttpServletRequest request){
    Response response = new Response();
    String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    Map<String, Object> result = userService.verify(authorizationHeader);

    if (!(boolean)result.get("result")) {
      response.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setData(result.get("msg").toString());
    }
    else {
      response.setStatus(HttpStatus.OK.value());
      response.setData(result.get("email").toString());
    }

    return ResponseEntity.ok(response);
  }
}
