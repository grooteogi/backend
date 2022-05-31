package grooteogi.dto;

import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

public class ReviewDto {

  @Data
  @Builder
  public static class Request {

    @NotNull
    private Integer postId;

    @NotNull
    private Integer reservationId;

    @NotNull
    private String text;

    @NotNull
    private Long score;
  }

  @Data
  @Builder
  public static class Response {
    private int reviewId;
    private int score;
    private String nickname;
    private String imageUrl;
    private String createAt;
    private String text;
  }
}