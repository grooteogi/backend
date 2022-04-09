package grooteogi.utils;

import grooteogi.dto.OauthDto;
import grooteogi.dto.UserDto;
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

    return null;
  }

  private UserDto googleAuth(OauthDto oauthDto) {

    return null;
  }
}
