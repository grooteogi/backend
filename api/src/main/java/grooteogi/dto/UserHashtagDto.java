package grooteogi.dto;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserHashtagDto {

  @NotBlank(message = "user id를 입력하세요.")
  private Integer userId;

  @NotBlank(message = "hashtag id를 입력하세요.")
  private Integer[] hashtagIds;

}
