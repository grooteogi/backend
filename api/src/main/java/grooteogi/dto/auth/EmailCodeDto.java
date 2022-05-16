package grooteogi.dto.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
public class EmailCodeDto {

  @Data
  @Builder
  public static class Request {
    @Email
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "인증코드를 입력해주세요.")
    private String code;
  }

  @Data
  @Builder
  public static class Response {

  }


}
