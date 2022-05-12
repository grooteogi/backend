package grooteogi.dto;

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
    private String imageUrl;
    private String title;
    private List<String> hashtags;
    private String date;
    private String startTime;
    private String endTime;
    private String place;
  }

  @Data
  @Builder
  public static class SmsCode {
    private String code;
  }
}