package grooteogi.dto;

import grooteogi.enums.CreditType;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

public class PostDto {

  @Data
  @Builder
  public static class Request {

    @NotBlank(message = "제목을 입력하세요.")
    private String title;

    @NotBlank(message = "내용을 입력하세요.")
    private String content;

    private String imageUrl;
    private String[] hashtags;
    private CreditType creditType;
    private List<ScheduleDto.Request> schedules;

  }

  @Data
  @Builder
  public static class CreateResponse {

    private Integer postId;
  }

  @Data
  @Builder
  public static class SearchPost {

    private int postId;
    private String title;
    private String content;
    private String imageUrl;
    private List<String> hashtags;
  }

  @Data
  @Builder
  public static class SearchResponse {

    private List<SearchPost> posts;
    private int pageCount;
  }

  @Data
  @Builder
  public static class Response {

    private int postId;
    private String title;
    private String content;
    private String imageUrl;
    private String createAt;
    private String[] hashtags;
    private CreditType creditType;
    private LikeDto.Response likes;
    private UserDto.Response mentor;
  }

  @Data
  @Builder
  public static class ReviewResponse {

    private int reviewId;
    private int score;
    private String nickname;
    private String imageUrl;
    private String createAt;
    private String text;
  }
}
