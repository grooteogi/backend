package grooteogi.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;


public class AuthDto {

  @Data
  @Builder
  public static class Request {
    @NotBlank(message = "이메일 주소를 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$",
        message = "이메일 형식에 맞게 입력해주세요.")
    private String email;

    @NotNull(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*\\W)(?=\\S+$).{8,}",
        message = "비밀번호는 영어 소문자, 숫자, 특수기호를 포함하여 최소 8글자 이상이어야 합니다.")
    private String password;
  }

  @Data
  @Builder
  public static class Response {
    private String nickname;
    private String imageUrl;
  }

  @Data
  @Builder
  public static class SendEmailRequest {
    @Email
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;
  }

  @Data
  @Builder
  public static class CheckEmailRequest {
    @Email
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "인증코드를 입력해주세요.")
    private String code;
  }

  @Data
  @Builder
  public static class Oauth {
    @Email
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "인증코드를 입력해주세요.")
    private String code;
  }
}
