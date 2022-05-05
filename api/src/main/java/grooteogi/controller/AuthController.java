package grooteogi.controller;

import grooteogi.domain.User;
import grooteogi.dto.OauthDto;
import grooteogi.dto.auth.EmailCodeDto;
import grooteogi.dto.auth.LoginDto;
import grooteogi.dto.auth.Token;
import grooteogi.dto.auth.UserDto;
import grooteogi.enums.LoginType;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.response.BasicResponse;
import grooteogi.service.AuthService;
import grooteogi.service.UserService;
import grooteogi.utils.OauthClient;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RestController
public class AuthController {

  private final UserService userService;
  private final AuthService authService;
  private final OauthClient oauthClient;

  @PostMapping("/auth/login")
  public ResponseEntity<BasicResponse> login(@RequestBody LoginDto loginDto) {
    User user = userService.getUserByEmail(loginDto.getEmail());
    Token token = authService.login(user, loginDto);

    HttpHeaders responseHeaders = setHeader(token, true);

    return new ResponseEntity<>(BasicResponse.builder().data(user).build(), responseHeaders,
        HttpStatus.OK);
  }

  @PostMapping("/auth/register")
  public ResponseEntity<BasicResponse> register(@Valid @RequestBody UserDto userDto,
      BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      throw new ApiException(ApiExceptionEnum.BAD_REQUEST_EXCEPTION);
    }

    User user = authService.register(userDto);

    return ResponseEntity.ok(BasicResponse.builder().data(user).build());
  }

  @DeleteMapping("/auth/withdrawal")
  public ResponseEntity withdrawal(@RequestParam("user-id") Integer userId) {
    authService.withdrawal(userId);

    return ResponseEntity.ok(BasicResponse.builder().build());
  }

  @GetMapping("/auth/token/verify")
  public ResponseEntity<BasicResponse> tokenVerify(HttpServletRequest request) {
    String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    Map<String, Object> result = authService.tokenVerify(authorizationHeader);

    if (!(boolean) result.get("result")) {
      throw new ApiException((ApiExceptionEnum) result.get("status"));
    }

    User user = userService.getUser(Integer.parseInt(result.get("ID").toString()));

    return ResponseEntity.ok(BasicResponse.builder().data(user).build());
  }

  @GetMapping("/auth/token/refresh")
  public ResponseEntity<BasicResponse> tokenRefresh(
      @RequestHeader(value = "REFRESH-TOKEN") String refreshToken, HttpServletRequest request) {
    String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    Map<String, Object> result = authService.tokenRefresh(authorizationHeader, refreshToken);

    if (!(boolean) result.get("result")) {
      throw new ApiException((ApiExceptionEnum) result.get("status"));
    }

    Token token = new Token();
    token.setAccessToken(result.get("token").toString());

    HttpHeaders responseHeaders = setHeader(token, false);
    User user = userService.getUser(Integer.parseInt(result.get("ID").toString()));

    return new ResponseEntity<>(BasicResponse.builder().data(user).build(), responseHeaders,
        HttpStatus.OK);
  }

  @GetMapping("/auth/email")
  public ResponseEntity<BasicResponse> sendVerifyEmail(
      @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
      @RequestParam String address) {

    if (userService.isExistEmail(address)) {
      throw new ApiException(ApiExceptionEnum.EMAIL_DUPLICATION_EXCEPTION);
    }

    authService.sendVerifyEmail(address);

    return ResponseEntity.ok(
        BasicResponse.builder().message("send email verification success").build());
  }

  @PostMapping("/auth/email")
  public ResponseEntity<BasicResponse> checkVerifyEmail(
      @RequestBody EmailCodeDto emailCodeRequest) {
    authService.checkVerifyEmail(emailCodeRequest);

    return ResponseEntity.ok(
        BasicResponse.builder().message("confirm email verification success").build());
  }

  @GetMapping("/oauth/{type}")
  public ResponseEntity<BasicResponse> oauth(
      @PathVariable String type, @RequestParam("code") String code) {
    OauthDto oauthDto = new OauthDto();
    oauthDto.setCode(code);
    UserDto userDto;

    if (type.toUpperCase().equals(LoginType.GOOGLE)) {
      oauthDto.setType(LoginType.GOOGLE);
      userDto = oauthClient.googleToken(oauthDto);
    } else if (type.toUpperCase().equals(LoginType.KAKAO)) {
      oauthDto.setType(LoginType.KAKAO);
      userDto = oauthClient.kakaoToken(oauthDto);
    } else {
      throw new ApiException(ApiExceptionEnum.NOT_FOUND_EXCEPTION);
    }

    Map<String, Object> result = authService.oauth(userDto);

    Token token = (Token) result.get("token");
    HttpHeaders responseHeaders = setHeader(token, true);

    return new ResponseEntity<>(BasicResponse.builder().data((User) result.get("user")).build(),
        responseHeaders, HttpStatus.OK);
  }

  private HttpHeaders setHeader(Token token, boolean isRefresh) {
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("X-AUTH-TOKEN", token.getAccessToken());

    if (isRefresh) {
      ResponseCookie responseCookie = ResponseCookie.from("X-REFRESH-TOKEN",
          token.getRefreshToken()).httpOnly(true).secure(true).path("/").build();
      responseHeaders.set(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }

    return responseHeaders;
  }
}
