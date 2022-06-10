package grooteogi.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import grooteogi.dto.auth.OauthDto;
import grooteogi.enums.LoginType;
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
  @Value("${custom.oauth2.redirect.back}")
  private String redirectUrl;

  @Autowired
  public OauthClient(RestTemplateBuilder restTemplateBuilder,
      HttpServletResponse httpServletResponse) {
    this.restTemplate = restTemplateBuilder.build();
    this.httpServletResponse = httpServletResponse;
  }

  public OauthDto kakaoToken(String code) {
    String accessToken = "";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "authorization_code");
    params.add("client_id", kakaoClientId);
    params.add("client_secret", kakaoClientSecret);
    params.add("redirect_uri", redirectUrl + "/oauth/kakao");
    params.add("code", code);

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

    String url = "https://kauth.kakao.com/oauth/token";
    try {
      ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
      JsonParser parser = new JsonParser();
      JsonElement element = parser.parse(response.getBody());
      accessToken = element.getAsJsonObject().get("access_token").getAsString();
      OauthDto oauthDto = kakaoAuth(accessToken);
      oauthDto.setType(LoginType.KAKAO);
      return oauthDto;
    } catch (RestClientException e) {
      e.printStackTrace();
      throw new ApiException(ApiExceptionEnum.LOGIN_FAIL_EXCEPTION);
    }
  }

  private OauthDto kakaoAuth(String token) {
    OauthDto oauthDto = new OauthDto();
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

      oauthDto.setNickname(properties.get("nickname").getAsString());
      oauthDto.setEmail(kakaoAccount.get("email").getAsString());
      oauthDto.setPassword(token);
    } catch (RestClientException e) {
      e.printStackTrace();
      throw new ApiException(ApiExceptionEnum.UNAUTHORIZED_EXCEPTION);
    }
    return oauthDto;
  }

  public void googleRequest() {
    Map<String, Object> params = new HashMap<>();
    params.put("scope", "profile https://www.googleapis.com/auth/userinfo.email");
    params.put("response_type", "code");
    params.put("client_id", googleClientId);
    params.put("redirect_uri", redirectUrl + "/oauth/google");

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

  public OauthDto googleToken(String code) {
    String accessToken = "";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "authorization_code");
    params.add("client_id", googleClientId);
    params.add("client_secret", googleClientSecret);
    params.add("redirect_uri", redirectUrl + "/oauth/google");
    params.add("code", code);
    params.add("state", "url_parameter");

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

    String url = "https://oauth2.googleapis.com/token";
    try {
      ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
      JsonParser parser = new JsonParser();
      JsonElement element = parser.parse(response.getBody());
      accessToken = element.getAsJsonObject().get("access_token").getAsString();
      OauthDto oauth = googleAuth(accessToken);
      oauth.setType(LoginType.GOOGLE);
      return oauth;
    } catch (RestClientException e) {
      e.printStackTrace();
      throw new ApiException(ApiExceptionEnum.LOGIN_FAIL_EXCEPTION);
    }
  }

  private OauthDto googleAuth(String token) {
    OauthDto oauth = new OauthDto();
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

      oauth.setNickname(element.getAsJsonObject().get("name").getAsString());
      oauth.setEmail(element.getAsJsonObject().get("email").getAsString());
    } catch (RestClientException e) {
      e.printStackTrace();
      throw new ApiException(ApiExceptionEnum.UNAUTHORIZED_EXCEPTION);
    }
    return oauth;
  }
}
