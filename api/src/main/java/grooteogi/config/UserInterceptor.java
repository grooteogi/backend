package grooteogi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.response.RefreshResponse;
import grooteogi.utils.JwtProvider;
import grooteogi.utils.Session;
import java.net.URLDecoder;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.WebUtils;

@Component
@RequiredArgsConstructor
public class UserInterceptor implements HandlerInterceptor {

  private final JwtProvider jwtProvider;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) throws Exception {
    String jwt = jwtProvider.extractToken(request.getHeader(HttpHeaders.AUTHORIZATION));

    if (jwt != null) {
      boolean flag = jwtProvider.isUsable(jwt);
      if (flag) {
        SecurityContextHolder.getContext().setAuthentication(
            new JwtAuthentication(jwtProvider.extractAllClaims(jwt)));
      } else {
        Cookie refreshCookie = WebUtils.getCookie(request, "X-REFRESH-TOKEN");
        if (refreshCookie != null) {
          String refreshToken = URLDecoder.decode(refreshCookie.getValue(), "UTF-8");
          Session session = jwtProvider.extractAllClaims(refreshToken);
          response.setStatus(HttpServletResponse.SC_ACCEPTED);
          response.setHeader("X-AUTH-TOKEN",
              jwtProvider.generateRefreshToken(session.getId(), session.getEmail()));

          RefreshResponse refreshResponse = new RefreshResponse();
          refreshResponse.setMessage("token refresh success");

          ObjectMapper mapper = new ObjectMapper();
          String result = mapper.writeValueAsString(refreshResponse);
          response.getWriter().write(result);
        } else {
          System.out.println("refresh is Null");
          throw new ApiException(ApiExceptionEnum.EXPIRED_TOKEN_EXCEPTION);
        }
      }
      return flag;
    } else {
      throw new ApiException(ApiExceptionEnum.ACCESS_DENIED_EXCEPTION);
    }
  }
}
