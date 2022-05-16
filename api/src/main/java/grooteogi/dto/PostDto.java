package grooteogi.dto;

import grooteogi.domain.Review;
import grooteogi.enums.CreditType;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;


public class PostDto {

  @Data
  @Builder
  public static class Request {

    @NotBlank(message = "user id를 입력하세요.")
    private Integer userId;

    @NotBlank(message = "제목을 입력하세요.")
    private String title;

    @NotBlank(message = "내용을 입력하세요.")
    private String content;

    private CreditType credit;

    private String imageUrl;

    private String[] hashtags;

    private List<ScheduleDto.Request> schedules;
    
  }

  @Data
  @Builder
  public static class CreateResponse {

    private Integer postId;

  }

  @Data
  @Builder
  public static class SearchResponse {

    private int postId;

    private String title;

    private String content;

    private String imageUrl;

    private List<String> hashtags;

  }

  @Data
  @Builder
  public static class DetailResponse {

    private int postId;

    private String title;

    private String content;

    private String imageUrl;

    private String createAt;

    private CreditType creditType;

    private int likes;

    private UserDto.Response mentor;

    private List<Review> reviews;

    private List<ScheduleDto.Response> schedules;
  }
}
