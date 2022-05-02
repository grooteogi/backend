package grooteogi.dto.hashtag;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HashtagDto {

  @NotBlank(message = "추가할 해시태그를 입력해주세요.")
  private String tag;

}
