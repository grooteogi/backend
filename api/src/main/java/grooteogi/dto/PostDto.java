package grooteogi.dto;

import grooteogi.enums.CreditType;
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

    private Object[] schedules;
  }

}
