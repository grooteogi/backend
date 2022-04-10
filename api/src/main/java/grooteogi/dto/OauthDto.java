package grooteogi.dto;

import grooteogi.enums.LoginType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OauthDto {

  @NotNull(message = "")
  private LoginType type;

  @NotBlank(message = "")
  private String token;
}
