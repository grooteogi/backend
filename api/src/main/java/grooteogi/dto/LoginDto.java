package grooteogi.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
public class LoginDto {

    @NotBlank(message = "이메일 주소를 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
