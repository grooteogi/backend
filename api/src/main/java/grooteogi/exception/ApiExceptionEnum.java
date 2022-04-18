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
  LOGIN_FAIL_EXCEPTION(HttpStatus.NOT_FOUND, "로그인에 실패하였습니다."),
  USER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
  EMAIL_DUPLICATION_EXCEPTION(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
  EXPIRED_TOKEN_EXCEPTION(HttpStatus.INSUFFICIENT_SPACE_ON_RESOURCE, "만료된 토큰입니다."), //419
  EXPIRED_REFRESH_TOKEN_EXCEPTION(HttpStatus.INSUFFICIENT_SPACE_ON_RESOURCE,
      "만료된 토큰입니다. 회원가입을 다시 시도하십시오."), //419
  NO_EXPIRED_TOKEN_EXCEPTION(HttpStatus.BAD_REQUEST, "만료되지 않은 토큰입니다."), //
  S3_UPLOAD_FAIL_EXCEPTION(HttpStatus.BAD_REQUEST, "파일 업로드에 실패했습니다."), //
  DUPLICATION_VALUE_EXCEPTION(HttpStatus.CONFLICT, "이미 존재하는 값입니다."),
  USERHASHTAG_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "저장된 해당 사용자 해시태그가 없습니다."),
  POST_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "저장된 해당 포스트가 없습니다."),
  HASHTAG_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "저장된 해당 해시태그가 없습니다.");

  private final HttpStatus httpStatus;
  private final Integer status;
  private final String message;

  ApiExceptionEnum(HttpStatus httpStatus, String message) {
    this.httpStatus = httpStatus;
    this.status = httpStatus.value();
    this.message = message;
  }
}
