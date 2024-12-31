package school.hei.asa.endpoint.rest.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class CustomHeaderRequestWrapper extends HttpServletRequestWrapper {

  private final Map<String, String> customHeaders;

  public CustomHeaderRequestWrapper(HttpServletRequest request) {
    super(request);
    this.customHeaders = new HashMap<>();
  }

  public void addHeader(String name, String value) {
    customHeaders.put(name, value);
  }

  @Override
  public String getHeader(String name) {
    String headerValue = customHeaders.get(name);
    return headerValue != null ? headerValue : super.getHeader(name);
  }

  @Override
  public Enumeration<String> getHeaderNames() {
    // Combine custom headers with the original headers
    Map<String, String> allHeaders = new HashMap<>(customHeaders);
    Enumeration<String> originalHeaderNames = super.getHeaderNames();
    while (originalHeaderNames.hasMoreElements()) {
      String headerName = originalHeaderNames.nextElement();
      allHeaders.putIfAbsent(headerName, super.getHeader(headerName));
    }
    return Collections.enumeration(allHeaders.keySet());
  }

  @Override
  public Enumeration<String> getHeaders(String name) {
    if (customHeaders.containsKey(name)) {
      return Collections.enumeration(Collections.singletonList(customHeaders.get(name)));
    }
    return super.getHeaders(name);
  }
}
