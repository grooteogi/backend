package grooteogi.utils;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Session implements Serializable {
  private int id;
  private String email;
}
