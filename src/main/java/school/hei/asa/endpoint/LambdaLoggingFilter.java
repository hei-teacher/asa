package school.hei.asa.endpoint;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class LambdaLoggingFilter {

  private static final Logger log = LoggerFactory.getLogger(LambdaLoggingFilter.class);

  @Bean
  public Filter logHeadersFilter() {
    return (ServletRequest request, ServletResponse response, FilterChain chain) -> {
      HttpServletRequest httpReq = (HttpServletRequest) request;

      log.info(
          "[LAMBDA LOG] Incoming request: {} {}", httpReq.getMethod(), httpReq.getRequestURI());

      Collections.list(httpReq.getHeaderNames())
          .forEach(name -> log.info("[LAMBDA LOG] Header: {} = {}", name, httpReq.getHeader(name)));

      chain.doFilter(request, response);
    };
  }
}
