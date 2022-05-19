package grooteogi.dto;

import lombok.Builder;
import lombok.Data;

public class LikeDto {

  @Data
  @Builder
  public static class Response {

    private int count;
    private boolean liked;
  }
}
