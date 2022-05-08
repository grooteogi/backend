package grooteogi.dto;

import lombok.Builder;
import lombok.Data;

public class ProfileDto {
  @Data
  @Builder
  public static class Request {
    private String nickname;
    private String imageUrl;
    private String name;
    private String address;
    private String contact;
  }

  @Data
  @Builder
  public static class Response {

  }
}
