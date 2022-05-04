package grooteogi.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReservationDto {
  @NotNull
  private Integer scheduleId;
  private String message;
}
