package grooteogi.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import grooteogi.dto.OauthDto;
import grooteogi.dto.UserDto;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OauthClient {
  public UserDto authenticate(OauthDto oauthDto) {
    switch (oauthDto.getType()) {
      case KAKAO:
        return kakaoAuth(oauthDto);

      case GOOGLE:
        return googleAuth(oauthDto);

      default:
        return null;
    }
  }

  private UserDto kakaoAuth(OauthDto oauthDto) {
    HashMap<String, Object> userInfo = new HashMap<String, Object>();
    UserDto userDto = new UserDto();
    try {
      URL url = new URL("https://kapi.kakao.com/v2/user/me");
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Authorization", "Bearer " + oauthDto.getToken());

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

      userDto.setType(oauthDto.getType());
      userDto.setNickname(properties.getAsJsonObject().get("nickname").getAsString());
      userDto.setEmail(kakaoAccount.getAsJsonObject().get("email").getAsString());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return userDto;
  }

  private UserDto googleAuth(OauthDto oauthDto) {

    return null;
  }
}
