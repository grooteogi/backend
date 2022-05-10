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
  public static class Response {

    private String title;

    private String content;

    private String imageUrl;

    private int views;

    private CreditType credit;

    private String nickname;

    private String date;

  }
}
