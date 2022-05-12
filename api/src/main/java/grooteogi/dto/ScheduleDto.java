package grooteogi.dto;

import lombok.Builder;
import lombok.Data;

public class ScheduleDto {

  @Data
  @Builder
  public static class Request {

    private String date;

    private String startTime;

    private String endTime;

    private String region;

    private String place;

  }

  @Data
  @Builder
  public static class Response {

  }
}
