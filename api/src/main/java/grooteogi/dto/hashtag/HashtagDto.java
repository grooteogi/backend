package grooteogi.dto.hashtag;

import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HashtagDto {

  @Data
  @Builder
  public static class Request {
    @NotNull
    private String name;
  }

  @Data
  @Builder
  public static class Response {

    private int hashtagId;
    private String name;

  }

}
