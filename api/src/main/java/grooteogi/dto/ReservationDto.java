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
  public static class Responses {
    private String title;
    private String date;
    private String startTime;
    private String endTime;
    private String region;
    private String place;
    private List<String> hashtags;
    private int postId;
    private String imageUrl;
    private String status;
  }

  @Data
  @Builder
  public static class Response {
    private int reservationId;
  }

  @Data
  @Builder
  public static class SmsCode {
    private String code;
  }
}