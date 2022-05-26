package grooteogi.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshResponse {

  private final Integer status = HttpStatus.ACCEPTED.value();

  private String message;
}

