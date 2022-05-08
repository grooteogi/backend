package grooteogi.dto;

import grooteogi.enums.CreditType;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostDto {

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
