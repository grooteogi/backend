package grooteogi.dto;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyingPostDto {

  @NotBlank(message = "post id를 입력하세요.")
  private Integer postId;

  @NotBlank(message = "제목을 입력하세요.")
  private String title;

  @NotBlank(message = "내용을 입력하세요.")
  private String content;

  private String imageUrl;

  private Integer[] hashtagId;

}
