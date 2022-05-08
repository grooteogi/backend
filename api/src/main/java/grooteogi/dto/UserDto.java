package grooteogi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserDto {

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Password {
    String password;

  }

  @Data
  @Builder
  public static class Response {

  }
}
