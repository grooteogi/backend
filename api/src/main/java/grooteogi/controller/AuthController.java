package grooteogi.controller;

import grooteogi.domain.User;
import grooteogi.dto.EmailCodeDto;
import grooteogi.dto.LoginDto;
import grooteogi.dto.Token;
import grooteogi.dto.UserDto;
import grooteogi.response.BasicResponse;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.service.EmailService;
import grooteogi.service.UserService;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {

  private final UserService userService;
  private final EmailService emailService;

  @PostMapping("/login")
  public ResponseEntity<BasicResponse> login(@RequestBody LoginDto loginDto) {
    Token token = userService.login(loginDto);
    User user = userService.getUserByEmail(loginDto.getEmail());

    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("X-AUTH-TOKEN", token.getAccessToken());
    ResponseCookie responseCookie = ResponseCookie.from("X-REFRESH-TOKEN", token.getRefreshToken())
        .httpOnly(true).secure(true).path("/").build();
    responseHeaders.set(HttpHeaders.SET_COOKIE, responseCookie.toString());

    return new ResponseEntity<>(BasicResponse.builder().data(user).build(), responseHeaders,
        HttpStatus.OK);
  }

  @PostMapping("/register")
  public ResponseEntity<BasicResponse> register(@Valid @RequestBody UserDto userDto,
      BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      throw new ApiException(ApiExceptionEnum.BAD_REQUEST_EXCEPTION);
    }

    User user = userService.register(userDto);

    return ResponseEntity.ok(BasicResponse.builder().data(user).build());
  }

  @DeleteMapping("/withdrawal")
  public ResponseEntity withdrawal(@RequestParam("user-id") Integer userId) {
    userService.withdrawal(userId);

    return ResponseEntity.ok(BasicResponse.builder().build());
  }

  @GetMapping("/token/verify")
  public ResponseEntity<BasicResponse> tokenVerify(HttpServletRequest request) {
    String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    Map<String, Object> result = userService.tokenVerify(authorizationHeader);

    if (!(boolean) result.get("result")) {
      throw new ApiException((ApiExceptionEnum) result.get("status"));
    }

    User user = userService.getUser(Integer.parseInt(result.get("ID").toString()));

    return ResponseEntity.ok(BasicResponse.builder().data(user).build());
  }

  @GetMapping("/token/refresh")
  public ResponseEntity<BasicResponse> tokenRefresh(
      @RequestHeader(value = "REFRESH-TOKEN") String refreshToken, HttpServletRequest request) {
    String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    Map<String, Object> result = userService.tokenRefresh(authorizationHeader, refreshToken);

    if (!(boolean) result.get("result")) {
      throw new ApiException((ApiExceptionEnum) result.get("status"));
    }

    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("X-AUTH-TOKEN", result.get("token").toString());
    User user = userService.getUser(Integer.parseInt(result.get("ID").toString()));

    return new ResponseEntity<>(BasicResponse.builder().data(user).build(), responseHeaders,
        HttpStatus.OK);
  }

  @GetMapping("/email")
  public ResponseEntity<BasicResponse> sendVerifyEmail(
      @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
      @RequestParam String address) {

    emailService.sendVerifyEmail(address);

    return ResponseEntity.ok(
        BasicResponse.builder().message("send email verification success").build());
  }

  @PostMapping("/email")
  public ResponseEntity<BasicResponse> checkVerifyEmail(
      @RequestBody EmailCodeDto emailCodeRequest) {
    emailService.checkVerifyEmail(emailCodeRequest);

    return ResponseEntity.ok(
        BasicResponse.builder().message("confirm email verification success").build());
  }
}
