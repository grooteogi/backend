package grooteogi.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EmailDto {

  @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$",
      message = "이메일 형식에 맞게 입력해주세요.")
  @NotBlank(message = "이메일을 입력해주세요.")
  private String email;
}
