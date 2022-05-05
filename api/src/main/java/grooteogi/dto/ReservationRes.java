package grooteogi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationRes {

  private int id;
  private String imgUrl;
  private String title;
//  private String[] hashtags;
  private String date;
  private String startTime;
  private String region;

}
