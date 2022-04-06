package grooteogi.controller;


import grooteogi.domain.User;
import grooteogi.dto.EmailCodeRequest;
import grooteogi.dto.EmailRequest;
import grooteogi.dto.LoginDto;
import grooteogi.dto.Token;
import grooteogi.dto.UserDto;
import grooteogi.dto.response.BasicResponse;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.service.EmailService;
import grooteogi.service.UserService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserController {

  private final UserService userService;
  private final EmailService emailService;

  @GetMapping
  public ResponseEntity<BasicResponse> getAllUser() {
    List<User> userList = userService.getAllUser();

    return ResponseEntity.ok(BasicResponse.builder().count(userList.size()).data(userList).build());
  }

  @GetMapping("/{userId}")
  public ResponseEntity<BasicResponse> getUser(@PathVariable Integer userId) {
    User user = userService.getUser(userId);

    return ResponseEntity.ok(BasicResponse.builder().data(user).build());
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity deleteUser(@PathVariable Integer userId) {
    userService.withdrawal(userId);

    return ResponseEntity.ok(BasicResponse.builder().build());
  }

  /*
  일반 회원가입 중 이메일 인증 버튼 누를 경우 ( 유효성 검사, 이메일 중복 검사 )
  */
  @PostMapping("/email-verification/create")
  public ResponseEntity<BasicResponse> createEmailVerification(
      @Valid @RequestBody EmailRequest email, BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      throw new ApiException(ApiExceptionEnum.BAD_REQUEST_EXCEPTION);
    }

    if (emailService.isExist(email)) {
      throw new ApiException(ApiExceptionEnum.EMAIL_DUPLICATION_EXCEPTION);
    }

    emailService.createEmailVerification(email);

    return ResponseEntity.ok(
        BasicResponse.builder().message("send email verification success").build());
  }

  /*
  일반 회원가입 중 이메일 인증 버튼을 누를 경우 ( 인증 코드 확인 )
  */
  @PostMapping("/email-verification/confirm")
  public ResponseEntity<BasicResponse> confirmEmailVerification(
      @RequestBody EmailCodeRequest emailCodeRequest) {

    if (!emailService.confirmEmailVerification(emailCodeRequest)) {
      throw new ApiException(ApiExceptionEnum.EXPIRED_TOKEN_EXCEPTION);
    }

    return ResponseEntity.ok(BasicResponse.builder().build());
  }

  /*
  일반 회원가입 중 가입 버튼을 누를 경우 ( 비밀번호 유효성 검사 )
  TODO 일반 회원가입과 OAuth 회원가입의 dto를 다르게 할지 결정 필요. 다르게 할 시 api도 다르게
   */
  @PostMapping("/register")
  public ResponseEntity<BasicResponse> register(@Valid @RequestBody UserDto userDto,
      BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      throw new ApiException(ApiExceptionEnum.BAD_REQUEST_EXCEPTION);
    }

    User user = userService.register(userDto);

    return ResponseEntity.ok(BasicResponse.builder().data(user).build());
  }

  @PostMapping("/login")
  public ResponseEntity<BasicResponse> login(@RequestBody LoginDto loginDto) {
    Token token = userService.login(loginDto);
    User user = userService.getUserByEmail(loginDto.getEmail());

    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("X-AUTH-TOKEN", token.getAccessToken());
    responseHeaders.set("X-REFRESH-TOKEN", token.getRefreshToken());

    return new ResponseEntity<>(
        BasicResponse.builder().data(user).build(), responseHeaders, HttpStatus.OK
    );
  }

  @PostMapping("/oauth/register")
  public ResponseEntity<BasicResponse> oauthRegister(@Valid @RequestBody UserDto userDto,
      BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      throw new ApiException(ApiExceptionEnum.BAD_REQUEST_EXCEPTION);
    }

    User user = userService.register(userDto);

    return ResponseEntity.ok(BasicResponse.builder().data(user).build());
  }

  @PostMapping("/oauth/login")
  public ResponseEntity<BasicResponse> oauthLogin(@RequestBody LoginDto loginDto) {
    Token token = userService.login(loginDto);
    User user = userService.getUserByEmail(loginDto.getEmail());

    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("X-AUTH-TOKEN", token.getAccessToken());
    responseHeaders.set("X-REFRESH-TOKEN", token.getRefreshToken());

    return new ResponseEntity<>(
        BasicResponse.builder().data(user).build(), responseHeaders, HttpStatus.OK
    );
  }

  @GetMapping("/token/verify")
  public ResponseEntity<BasicResponse> verify(HttpServletRequest request) {
    String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    Map<String, Object> result = userService.verify(authorizationHeader);

    if (!(boolean) result.get("result")) {
      throw new ApiException((ApiExceptionEnum) result.get("status"));
    }

    User user = userService.getUser(Integer.parseInt(result.get("ID").toString()));

    return ResponseEntity.ok(BasicResponse.builder().data(user).build());
  }

  @GetMapping("/token/refresh")
  public ResponseEntity<BasicResponse> refresh(
      @RequestHeader(value = "REFRESH-TOKEN") String refreshToken, HttpServletRequest request) {
    String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    Map<String, Object> result = userService.refresh(authorizationHeader, refreshToken);

    if (!(boolean) result.get("result")) {
      throw new ApiException((ApiExceptionEnum) result.get("status"));
    }

    Map<String, Object> returnValue = new HashMap<String, Object>();
    returnValue.put("token", result.get("token").toString());
    returnValue.put("user", userService.getUser(Integer.parseInt(result.get("ID").toString())));

    return ResponseEntity.ok(BasicResponse.builder().data(returnValue).build());
  }
}
