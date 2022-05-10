package grooteogi.mapper;

import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateMapper {

  public String asStringDate(Date date) {
    return date != null ? new SimpleDateFormat("yyyy-MM-dd")
        .format(date) : null;
  }

  public Date asDate(String date) {
    try {
      return date != null ? (Date) new SimpleDateFormat("yyyy-MM-dd")
          .parse(date) : null;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public String asStringTime(Time time) {
    return time != null ? new SimpleDateFormat("HH:mm:ss")
        .format(time) : null;
  }

  public Time adTime(String time) {
    try {
      return time != null ? (Time) new SimpleDateFormat("HH:mm:ss")
          .parse(time) : null;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }
}
