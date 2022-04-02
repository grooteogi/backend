package grooteogi.dto;

import grooteogi.enums.LoginType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

  @NotNull(message = "")
  private LoginType type;

  private String nickname;

  @NotBlank(message = "이메일 주소를 입력해주세요.")
  @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$",
      message = "이메일 형식에 맞게 입력해주세요.")
  private String email;

  //@NotNull(message = "비밀번호를 입력해주세요.")
  @Pattern(regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*\\W)(?=\\S+$).{8,}",
      message = "비밀번호는 영어 소문자, 숫자, 특수기호를 포함하여 최소 8글자 이상이어야 합니다.")
  private String password;

  private String token;
}
