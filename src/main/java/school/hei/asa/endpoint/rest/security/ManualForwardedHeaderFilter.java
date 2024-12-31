package school.hei.asa.endpoint.rest.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class ManualForwardedHeaderFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    if (request instanceof HttpServletRequest httpRequest) {
      var requestWrapper = new CustomHeaderRequestWrapper(httpRequest);
      requestWrapper.addHeader("X-Forwarded-Host", "asa.jcloudify.com");
      chain.doFilter(requestWrapper, response);
    } else {
      chain.doFilter(request, response);
    }
  }
}
