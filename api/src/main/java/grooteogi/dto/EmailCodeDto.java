package grooteogi.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailCodeDto {

  @Email
  @NotBlank(message = "이메일을 입력해주세요.")
  private String email;

  @NotBlank(message = "인증코드를 입력해주세요.")
  private String code;
}
