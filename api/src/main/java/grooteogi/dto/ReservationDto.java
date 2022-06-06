package grooteogi.dto;

import java.util.List;
import javax.validation.constraints.NotBlank;
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
  public static class DetailResponse {

    private int reservationId;
    private String title;
    private String date;
    private String startTime;
    private String endTime;
    private String region;
    private String place;
    private List<String> hashtags;
    private int postId;
    private String imageUrl;
    private Boolean isCanceled;
    private String hostPhone;
    private String applyPhone;
    private String applyNickname;
    private String text;
    private String review;
    private Long score;
  }

  @Data
  @Builder
  public static class Response {

    private int reservationId;
  }

  @Data
  @Builder
  public static class CheckSmsRequest {

    @NotBlank(message = "핸드폰 번호를 입력해주세요.")
    private String phoneNumber;

    @NotBlank(message = "인증코드를 입력해주세요.")
    private String code;
  }
}