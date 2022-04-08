package grooteogi.utils;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CursorResult<T> {
  private List<T> values;
  private Boolean hasNext;

  public CursorResult(List<T> values, Boolean hasNext) {
    this.values = values;
    this.hasNext = hasNext;
  }

}
