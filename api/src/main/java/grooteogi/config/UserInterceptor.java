package grooteogi.config;

import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.utils.JwtProvider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class UserInterceptor implements HandlerInterceptor {
  private final JwtProvider jwtProvider;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) throws Exception {
    String jwt = jwtProvider.extractToken(request.getHeader(HttpHeaders.AUTHORIZATION));

    if (jwt != null) {
      return jwtProvider.isUsable(jwt);
    } else {
      throw new ApiException(ApiExceptionEnum.ACCESS_DENIED_EXCEPTION);
    }
  }
}
