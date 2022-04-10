package grooteogi.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import grooteogi.dto.OauthDto;
import grooteogi.dto.UserDto;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OauthClient {

  @Value("${custom.oauth2.kakao.client-id}")
  private String kakaoClientId;

  public UserDto authenticate(OauthDto oauthDto) {
    switch (oauthDto.getType()) {
      case KAKAO:
        return kakaoToken(oauthDto);

      case GOOGLE:
        return googleAuth(oauthDto);

      default:
        throw new ApiException(ApiExceptionEnum.BAD_REQUEST_EXCEPTION);
    }
  }

  public UserDto kakaoToken(OauthDto oauthDto) {
    String accessToken = "";
    try {
      URL url = new URL("https://kauth.kakao.com/oauth/token");
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setDoOutput(true);
      final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
      StringBuilder sb = new StringBuilder();
      sb.append("grant_type=authorization_code");
      sb.append("&client_id=" + kakaoClientId);
      sb.append("&redirect_uri=http://localhost:8080/users"); // 임시 Redirect
      sb.append("&code=" + oauthDto.getCode());
      bw.write(sb.toString());
      bw.flush();
      BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String line = "";
      String result = "";
      while ((line = br.readLine()) != null) {
        result += line;
      }
      JsonParser parser = new JsonParser();
      JsonElement element = parser.parse(result);
      accessToken = element.getAsJsonObject().get("access_token").getAsString();
      br.close();
      bw.close();

      UserDto userDto = kakaoAuth(accessToken);
      userDto.setType(oauthDto.getType());
      return userDto;
    } catch (IOException e) {
      throw new ApiException(ApiExceptionEnum.UNAUTHORIZED_EXCEPTION);
    }
  }

  private UserDto kakaoAuth(String token) {
    UserDto userDto = new UserDto();
    try {
      URL url = new URL("https://kapi.kakao.com/v2/user/me");
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Authorization", "Bearer " + token);

      BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

      String line = "";
      String result = "";

      while ((line = br.readLine()) != null) {
        result += line;
      }

      JsonParser parser = new JsonParser();
      JsonElement element = parser.parse(result);

      JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
      JsonObject kakaoAccount = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

      userDto.setNickname(properties.getAsJsonObject().get("nickname").getAsString());
      userDto.setEmail(kakaoAccount.getAsJsonObject().get("email").getAsString());
    } catch (IOException e) {
      throw new ApiException(ApiExceptionEnum.UNAUTHORIZED_EXCEPTION);
    }
    return userDto;
  }

  private UserDto googleAuth(OauthDto oauthDto) {

    return null;
  }
}
