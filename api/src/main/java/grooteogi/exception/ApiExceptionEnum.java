package grooteogi.exception;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

/*
API 예외 처리를 정의합니다.
 */
@Getter
@ToString
public enum ApiExceptionEnum {
  // General Exception
  BAD_REQUEST_EXCEPTION(HttpStatus.BAD_REQUEST, "요청 변수를 확인해주세요."),
  UNAUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED, "인증이 실패하였습니다."),
  ACCESS_DENIED_EXCEPTION(HttpStatus.FORBIDDEN, "제한된 접근입니다."),
  NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "요청 URL을 확인해주세요."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버가 응답하지 않습니다."),

  // Custom Exception
  USER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
  EMAIL_DUPLICATION_EXCEPTION(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
  EXPIRED_TOKEN_EXCEPTION(HttpStatus.BAD_REQUEST, "만료된 토근입니다.");

  private final HttpStatus httpStatus;
  private final Integer status;
  private final String message;

  ApiExceptionEnum(HttpStatus httpStatus, String message) {
    this.httpStatus = httpStatus;
    this.status = httpStatus.value();
    this.message = message;
  }
}