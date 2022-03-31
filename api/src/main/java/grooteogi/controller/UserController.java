package grooteogi.controller;

import grooteogi.dto.*;
import grooteogi.domain.User;
import grooteogi.service.EmailService;
import grooteogi.service.UserService;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
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
  public List<User> getAllUser() {
    return userService.getAllUser();
  }

  // 일반 회원가입 중 이메일 인증 버튼 누를 경우 ( 유효성 검사, 이메일 중복 검사 )
  @PostMapping("/user/email/generate")
  public ResponseEntity genarateEmailVerify(@Valid @RequestBody EmailRequest email, BindingResult bindingResult){
    if(bindingResult.hasErrors()){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
      }
    else{
      if(emailService.genarateEmailVerify(email)) return ResponseEntity.status(HttpStatus.OK).body("success!!");
      else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("this email already exists");
    }
  }
   // 일반 회원가입 중 이메일 인증 버튼을 누를 경우 ( 인증 코드 확인 )
  @PostMapping("user/email/confirm")
  public ResponseEntity confirmEmailVerify(@RequestBody EmailCodeRequest emailCodeRequest){
    if(emailService.confirmEmailVerify(emailCodeRequest)){
      return ResponseEntity.status(HttpStatus.OK).body("confirm success!!!");
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("time out @@@");
  }
  
  
  // 일반 회원가입 중 가입 버튼을 누를 경우 ( 비밀번호 유효성 검사 )
  @PostMapping("user/register")
  public ResponseEntity register(@Valid @RequestBody UserDto userDto, BindingResult bindingResult){
    if(bindingResult.hasErrors()){
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
    }

    return ResponseEntity.status(HttpStatus.OK).body(userService.register( userDto ));
  }

  @PostMapping("user/register/oauth")
  public ResponseEntity registerOAuth(@Valid @RequestBody UserDto userDto, BindingResult bindingResult){
    if(bindingResult.hasErrors()){
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
    }

    return ResponseEntity.status(HttpStatus.OK).body(userService.register( userDto ));
  }

  @PostMapping("/login")
  public ResponseEntity login(@RequestBody LoginDto loginDto){
    Token token = userService.login(loginDto);
    if (token == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("오류");
    else return ResponseEntity.ok(token);
  }

  @GetMapping("/verify")
  public ResponseEntity verify(HttpServletRequest request){
    String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    Map<String, Object> result = userService.verify(authorizationHeader);

    if (!(boolean)result.get("result")) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.get("msg").toString());
    else return ResponseEntity.ok(result.get("email").toString());
  }
}
