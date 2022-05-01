package grooteogi.controller;

import grooteogi.domain.User;
import grooteogi.dto.OauthDto;
import grooteogi.dto.auth.Token;
import grooteogi.dto.auth.UserDto;
import grooteogi.enums.LoginType;
import grooteogi.response.BasicResponse;
import grooteogi.service.UserService;
import grooteogi.utils.OauthClient;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/oauth")
@RestController
public class OauthController {

  private final UserService userService;
  private final OauthClient oauthClient;

  @GetMapping("/kakao")
  public ResponseEntity<BasicResponse> oauthKakao(@RequestParam("code") String code) {
    OauthDto oauthDto = new OauthDto();
    oauthDto.setCode(code);
    oauthDto.setType(LoginType.KAKAO);

    UserDto userDto = oauthClient.kakaoToken(oauthDto);
    return oauth(userDto);
  }

  @GetMapping("/google")
  public ResponseEntity<BasicResponse> oauthGoogle(@RequestParam("code") String code) {
    OauthDto oauthDto = new OauthDto();
    oauthDto.setCode(code);
    oauthDto.setType(LoginType.GOOGLE);

    UserDto userDto = oauthClient.googleToken(oauthDto);
    return oauth(userDto);
  }

  private ResponseEntity oauth(UserDto userDto) {
    Map<String, Object> result = userService.oauth(userDto);

    Token token = (Token) result.get("token");

    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("X-AUTH-TOKEN", token.getAccessToken());
    ResponseCookie responseCookie = ResponseCookie.from("X-REFRESH-TOKEN", token.getRefreshToken())
        .httpOnly(true).secure(true).path("/").build();
    responseHeaders.set(HttpHeaders.SET_COOKIE, responseCookie.toString());

    return new ResponseEntity<>(BasicResponse.builder().data((User) result.get("user")).build(),
        responseHeaders, HttpStatus.OK);
  }
}
