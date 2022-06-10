package grooteogi.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
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

    @JsonCreator
    public Request(String name) {
      this.name = name;
    }
  }

  @Data
  @Builder
  public static class Response {

    private String name;

    @JsonCreator
    public Response(String name) {
      this.name = name;
    }
  }
}
