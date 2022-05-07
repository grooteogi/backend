package grooteogi.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationDto {
  @NotNull
  private Integer scheduleId;
  private String message;
}
