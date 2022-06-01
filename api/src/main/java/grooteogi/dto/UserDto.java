package grooteogi.dto;

import lombok.Builder;
import lombok.Data;

public class UserDto {

  @Data
  @Builder
  public static class PasswordRequest {

    String currentPassword;
    String newPassword;
  }

  @Data
  @Builder
  public static class Response {

    private int userId;
    private String nickname;
    private String imageUrl;
  }
}
