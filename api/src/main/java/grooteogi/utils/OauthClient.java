package grooteogi.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import grooteogi.dto.OauthDto;
import grooteogi.dto.UserDto;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OauthClient {
  private final RestTemplate restTemplate;
  private final HttpServletResponse httpServletResponse;

  @Value("${custom.oauth2.kakao.client-id}")
  private String kakaoClientId;
  @Value("${custom.oauth2.kakao.client-secret}")
  private String kakaoClientSecret;
  @Value("${security.oauth2.client.registration.google.client-id}")
  private String googleClientId;
  @Value("${security.oauth2.client.registration.google.client-secret}")
  private String googleClientSecret;

  @Autowired
  public OauthClient(RestTemplateBuilder restTemplateBuilder, HttpServletResponse httpServletResponse) {
    this.restTemplate = restTemplateBuilder.build();
    this.httpServletResponse = httpServletResponse;
  }

  public UserDto kakaoToken(OauthDto oauthDto) {
    String accessToken = "";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "authorization_code");
    params.add("client_id", kakaoClientId);
    params.add("client_secret", kakaoClientSecret);
    params.add("redirect_uri", "http://localhost:8080/user/oauth/kakao");
    params.add("code", oauthDto.getCode());

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

    String url = "https://kauth.kakao.com/oauth/token";
    try {
      ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
      JsonParser parser = new JsonParser();
      JsonElement element = parser.parse(response.getBody());
      accessToken = element.getAsJsonObject().get("access_token").getAsString();
      UserDto userDto = kakaoAuth(accessToken);
      userDto.setType(oauthDto.getType());
      return userDto;
    } catch (RestClientException e) {
      throw new ApiException(ApiExceptionEnum.LOGIN_FAIL_EXCEPTION);
    }
  }

  private UserDto kakaoAuth(String token) {
    UserDto userDto = new UserDto();
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

    String url = "https://kapi.kakao.com/v2/user/me";
    try {
      ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
      JsonParser parser = new JsonParser();
      JsonElement element = parser.parse(response.getBody());

      JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
      JsonObject kakaoAccount = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

      userDto.setNickname(properties.get("nickname").getAsString());
      userDto.setEmail(kakaoAccount.get("email").getAsString());
    } catch (RestClientException e) {
      throw new ApiException(ApiExceptionEnum.UNAUTHORIZED_EXCEPTION);
    }
    return userDto;
  }

  public void googleRequest(){
    Map<String, Object> params = new HashMap<>();
    params.put("scope", "profile https://www.googleapis.com/auth/userinfo.email");
    params.put("response_type", "code");
    params.put("client_id", googleClientId);
    params.put("redirect_uri", "http://localhost:8080/user/oauth/google");

    String parameterString = params.entrySet().stream()
        .map(x -> x.getKey() + "=" + x.getValue())
        .collect(Collectors.joining("&"));

    String url = "https://accounts.google.com/o/oauth2/v2/auth" + "?" + parameterString;

    try {
      httpServletResponse.sendRedirect(url);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public UserDto googleToken(OauthDto oauthDto) {
    String accessToken = "";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "authorization_code");
    params.add("client_id", googleClientId);
    params.add("client_secret", googleClientSecret);
    params.add("redirect_uri", "http://localhost:8080/user/oauth/google");
    params.add("code", oauthDto.getCode());
    params.add("state", "url_parameter");

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

    String url = "https://oauth2.googleapis.com/token";
    try {
      ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
      JsonParser parser = new JsonParser();
      JsonElement element = parser.parse(response.getBody());
      accessToken = element.getAsJsonObject().get("access_token").getAsString();
      UserDto userDto = googleAuth(accessToken);
      userDto.setType(oauthDto.getType());
      return userDto;
    } catch (RestClientException e) {
      throw new ApiException(ApiExceptionEnum.LOGIN_FAIL_EXCEPTION);
    }
  }

  public UserDto googleAuth(String token) {
    UserDto userDto = new UserDto();
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

    String url = "https://www.googleapis.com/oauth2/v3/userinfo";
    try {
      ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

      JsonParser parser = new JsonParser();
      JsonElement element = parser.parse(response.getBody());

      userDto.setNickname(element.getAsJsonObject().get("name").getAsString());
      userDto.setEmail(element.getAsJsonObject().get("email").getAsString());
    } catch (RestClientException e) {
      e.printStackTrace();
      throw new ApiException(ApiExceptionEnum.UNAUTHORIZED_EXCEPTION);
    }
    return userDto;
  }
}
