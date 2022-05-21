package grooteogi.controller;

import grooteogi.domain.User;
import grooteogi.dto.AuthDto;
import grooteogi.dto.auth.Oauthdto;
import grooteogi.dto.auth.Token;
import grooteogi.enums.LoginType;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.mapper.UserMapper;
import grooteogi.response.BasicResponse;
import grooteogi.service.AuthService;
import grooteogi.service.UserService;
import grooteogi.utils.OauthClient;
import javax.validation.Valid;
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
  public ResponseEntity<BasicResponse> login(@RequestBody AuthDto.Request request) {

    User user = userService.getUserByEmail(request);
    Token token = authService.login(user, request);

    AuthDto.Response response = UserMapper.INSTANCE.toResponseDto(user, user.getUserInfo());

    HttpHeaders responseHeaders = setHeader(token, true);

    return new ResponseEntity<>(BasicResponse.builder().data(response).build(), responseHeaders,
        HttpStatus.OK);
  }

  @PostMapping("/auth/register")
  public ResponseEntity<BasicResponse> register(@Valid @RequestBody AuthDto.Request request,
      BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      throw new ApiException(ApiExceptionEnum.BAD_REQUEST_EXCEPTION);
    }

    AuthDto.Response response = authService.register(request);

    return ResponseEntity.ok(BasicResponse.builder().data(response).build());
  }

  @DeleteMapping("/auth/withdrawal")
  public ResponseEntity withdrawal(@RequestParam("user-id") Integer userId) {
    authService.withdrawal(userId);

    return ResponseEntity.ok(BasicResponse.builder().build());
  }

  @PostMapping("/auth/email/send")
  public ResponseEntity<BasicResponse> sendVerifyEmail(
      @RequestBody AuthDto.SendEmailRequest request) {

    String email = request.getEmail();

    if (userService.isExistEmail(email)) {
      throw new ApiException(ApiExceptionEnum.EMAIL_DUPLICATION_EXCEPTION);
    }

    authService.sendVerifyEmail(email);

    return ResponseEntity.ok(
        BasicResponse.builder().message("send email success").build());
  }

  @PostMapping("/auth/email/check")
  public ResponseEntity<BasicResponse> checkVerifyEmail(
      @RequestBody AuthDto.CheckEmailRequest request) {
    authService.checkVerifyEmail(request);

    return ResponseEntity.ok(
        BasicResponse.builder().message("check email success").build());
  }

  @GetMapping("/oauth/{type}")
  public ResponseEntity<BasicResponse> oauth(
      @PathVariable String type, @RequestParam("code") String code) {

    Oauthdto oauthDto;

    if (type.equalsIgnoreCase(LoginType.GOOGLE.name())) {
      oauthDto = oauthClient.googleToken(code);
    } else if (type.equalsIgnoreCase(LoginType.KAKAO.name())) {
      oauthDto = oauthClient.kakaoToken(code);
    } else {
      throw new ApiException(ApiExceptionEnum.NOT_FOUND_EXCEPTION);
    }

    User user = authService.oauth(oauthDto);
    Token token = authService.generateToken(user.getId(), user.getEmail());

    AuthDto.Response response = UserMapper.INSTANCE.toResponseDto(user, user.getUserInfo());

    HttpHeaders responseHeaders = setHeader(token, true);

    return new ResponseEntity<>(BasicResponse.builder().data(response).build(),
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
