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
  NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "요청한 자원이 없습니다."),
  DUPLICATION_VALUE_EXCEPTION(HttpStatus.CONFLICT, "이미 존재하는 값입니다."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버가 응답하지 않습니다."),

  // Custom Exception
  // User Exception
  LOGIN_FAIL_EXCEPTION(HttpStatus.NOT_FOUND, "로그인에 실패하였습니다."),
  USER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
  EMAIL_DUPLICATION_EXCEPTION(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
  PASSWORD_VALUE_EXCEPTION(HttpStatus.BAD_REQUEST, "비밀번호는 영문과 특수문자 숫자를 포함하며 8자 이상이어야 합니다."),
  // Token Exception
  EXPIRED_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
  EXPIRED_REFRESH_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다. 회원가입을 다시 시도하십시오."),
  NO_EXPIRED_TOKEN_EXCEPTION(HttpStatus.BAD_REQUEST, "만료되지 않은 토큰입니다."),
  MALFORED_TOKEN_EXCEPTION(HttpStatus.BAD_REQUEST, "위조된 토큰입니다."),
  // S3 Exception
  S3_UPLOAD_FAIL_EXCEPTION(HttpStatus.BAD_REQUEST, "파일 업로드에 실패했습니다."),
  INVALID_FILE_FORMAT_EXCEPTION(HttpStatus.BAD_REQUEST, "잘못된 파일 형식입니다."),
  // UserInfo Exception
  USERINFO_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "유저 프로필 정보를 찾을 수 없습니다."),
  CONTACT_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "유저의 연락처 정보를 찾을 수 없습니다."),
  // Post Exception
  POST_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "저장된 해당 포스트가 없습니다."),
  HASHTAG_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "저장된 해당 해시태그가 없습니다."),
  NO_PERMISSION_EXCEPTION(HttpStatus.FORBIDDEN, "수정 혹은 삭제할 권한이 없습니다."),
  // Reservation Exception,
  DUPLICATION_RESERVATION_EXCEPTION(HttpStatus.CONFLICT, "신청 불가능한 일정입니다."),
  SCHEDULE_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 일정입니다."),
  RESERVATION_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "예약이 존재하지 않습니다."),
  RESERVATION_HOST_EXCEPTION(HttpStatus.BAD_REQUEST, "호스트는 예약을 신청할 수 없습니다."),
  NO_MODIFY_EXCEPTION(HttpStatus.FORBIDDEN, "변경 불가능한 예약입니다."),
  // Review Exception
  REVIEW_HOST_EXCEPTION(HttpStatus.BAD_REQUEST, "호스트는 리뷰를 남길 수 없습니다."),
  REVIEW_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "저장된 해당 리뷰가 없습니다."),
  NO_CREATE_REVIEW_EXCEPTION(HttpStatus.FORBIDDEN, "리뷰 작성 가능 기간이 아닙니다."),
  // Permission Exception
  INVALID_CODE_EXCEPTION(HttpStatus.NOT_FOUND, "인증코드를 다시 확인해주세요."),
  TIME_OUT_EXCEPTION(HttpStatus.BAD_REQUEST, "유효시간이 종료되었습니다.")
  ;

  private final HttpStatus httpStatus;
  private final Integer status;
  private final String message;

  ApiExceptionEnum(HttpStatus httpStatus, String message) {
    this.httpStatus = httpStatus;
    this.status = httpStatus.value();
    this.message = message;
  }
}
