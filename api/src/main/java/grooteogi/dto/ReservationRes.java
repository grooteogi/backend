package grooteogi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationRes {

  private int reservationId;
  private String imgUrl;
  private String title;
  private String[] hashtags;
  private String date;
  private String startTime;
  private String endTime;
  private String place;

}
