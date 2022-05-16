package grooteogi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JwtExpirationEnum {

  ACCESS_TOKEN_EXPIRATION_TIME("JWT 만료 시간 / 30분", 1000L * 60), // * 30 추가 필요 현재 테스트를 위해 1분으로 설정
  REDIS_TOKEN_EXPIRATION_TIME("REDIS 만료 시간 / 30분", 1L), // * 30 추가 필요 현재 테스트를 위해 1분으로 설정
  REFRESH_TOKEN_EXPIRATION_TIME("Refresh 토큰 만료 시간 / 7일", 1000L * 60 * 60 * 24 * 7);

  private String description;
  private Long value;
}
