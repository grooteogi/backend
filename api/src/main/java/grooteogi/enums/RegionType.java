package grooteogi.enums;

import java.util.Arrays;

public enum RegionType {
  GANGSEO("강서구"),
  GURO("구로구"),
  GEUMCHEON("금천구"),
  GWANAK("관악구"),
  DONGJAK("동작구"),
  YEONGDEUNGPO("영등포구"),
  YANGCHEON("양천구"),
  MAPO("마포구"),
  SEODAEMUN("서대문구"),
  SEOCHO("서초구"),
  GANGNAM("강남구"),
  SONGPA("송파구"),
  GANGDONG("강동구"),
  EUNPYEONG("은평구"),
  JONGNO("종로구"),
  JUNG("중구"),
  YONGSAN("용산구"),
  SEONGDONG("성동구"),
  DONGDAEMUN("동대문구"),
  JUNGNANG("중랑구"),
  GWANGJIN("광진구"),
  NOWON("노원구"),
  SEONGBUK("성북구"),
  GANGBUK("강북구"),
  DOBONG("도봉구")
  ;

  private final String label;

  RegionType(String label) {
    this.label = label;
  }

  public String toString() {
    return this.label;
  }

  public static RegionType getEnum(String label) {
    return Arrays.stream(RegionType.values())
        .filter(regionType -> regionType.label.equals(label))
        .findFirst().orElse(null);
  }

}
