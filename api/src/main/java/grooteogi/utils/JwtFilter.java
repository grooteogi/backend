package grooteogi.utils;

//import java.io.IOException;
//import java.util.Map;
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.GenericFilterBean;

public class JwtFilter {

}
//
//import java.io.IOException;
//import java.util.Map;
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.GenericFilterBean;
//
//@Component
//@RequiredArgsConstructor
//public class JwtFilter extends GenericFilterBean {
//
//  public static final String AUTHORIZATION_HEADER = "Authorization";
//  public static final String BEARER_PREFIX = "Bearer ";
//  private JwtProvider jwtProvider;
//
//  public JwtFilter(JwtProvider jwtProvider){
//    this.jwtProvider = jwtProvider;
//  }
//
//  @Override
//  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//      throws IOException, ServletException {
//    HttpServletResponse httpres = (HttpServletResponse) response;
//    HttpServletRequest httpreq = (HttpServletRequest) request;
//    String jwt =resolveToken((HttpServletRequest) request);
//
//    Map<String, Object> result = jwtProvider.verifyToken(httpreq.getHeader(AUTHORIZATION_HEADER));
//
//    System.out.println( "doFilter3" );
//    if (StringUtils.hasText(jwt) && (boolean) result.get("result")) {
//      httpres.setHeader("tokenSelf", result.get("ID").toString());
//
//      System.out.println( "doFilter4" );
//    }
//
//    System.out.println( "doFilter5" );
//    chain.doFilter(httpreq, httpres);
//  }
//
//  private String resolveToken(HttpServletRequest request) {
//    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
//    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
//      return bearerToken.substring(7);
//    }
//    return null;
//  }
//}
