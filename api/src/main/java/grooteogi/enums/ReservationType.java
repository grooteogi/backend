package grooteogi.enums;

import java.util.HashMap;
import java.util.Map;

public enum ReservationType {
  CANCELED(0),
  UNCANCELED(1),
  ;

  private Integer value;
  private static Map map = new HashMap();

  ReservationType(Integer value) {
    this.value = value;
  }

  static {
    for (ReservationType status : ReservationType.values()) {
      map.put(status.value, status);
    }
  }

  public static ReservationType valueOf(Integer status) {
    return (ReservationType) map.get(status);
  }

  public Integer getValue() {
    return this.value;
  }
}
