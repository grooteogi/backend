package grooteogi.dto;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserHashtagDto {
  @NotBlank(message = "hashtag를 입력하세요.")
  private String[] hashtags;
}
