package grooteogi.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

public class ReservationDto {

  @Data
  @Builder
  public static class Request {
    @NotNull
    private Integer scheduleId;
    private String message;
  }

  @Data
  @Builder
  public static class Response {
    private int reservationId;
    private String imgUrl;
    private String title;
    private List<String> hashtags;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String place;
  }
}