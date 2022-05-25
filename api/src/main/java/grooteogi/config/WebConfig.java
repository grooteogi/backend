package grooteogi.config;

import grooteogi.utils.JwtProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


public class WebConfig implements WebMvcConfigurer {

  @Configuration
  @Profile("local")
  public class LocalConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
      registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
    }
  }

  @Configuration
  @Profile("!local")
  @RequiredArgsConstructor
  public class ServerConfig implements WebMvcConfigurer {

    @Value("${spring.origin.url}")
    private String originUrl;

    @Value("${spring.interceptor.excludes}")
    private List<String> interceptorExcludes;

    private final JwtProvider jwtProvider;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
      registry.addMapping("/**").allowedOrigins(originUrl).allowedMethods("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
      registry.addInterceptor(new UserInterceptor(jwtProvider))
          .addPathPatterns("/**")
          .excludePathPatterns(interceptorExcludes);
    }
  }
}
