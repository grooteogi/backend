package grooteogi.config;

import grooteogi.utils.JwtProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  @Value("${spring.origin.url}")
  private String url;

  @Value("${spring.interceptor.excludes}")
  private List<String> excludes;

  private final JwtProvider jwtProvider;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**").allowedOrigins(url).allowedMethods("*");
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new UserInterceptor(jwtProvider))
        .addPathPatterns("/**")
        .excludePathPatterns(excludes);
  }
}
