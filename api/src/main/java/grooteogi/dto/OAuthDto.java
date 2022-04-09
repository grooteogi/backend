package grooteogi.dto;

import grooteogi.enums.LoginType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthDto {

  @NotNull(message = "")
  private LoginType type;

  @NotBlank(message = "")
  private String code;
}
