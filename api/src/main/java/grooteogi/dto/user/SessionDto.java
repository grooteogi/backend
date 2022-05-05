package grooteogi.dto.user;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionDto implements Serializable {
  private int id;
  private String nickname;
  private String email;
}
