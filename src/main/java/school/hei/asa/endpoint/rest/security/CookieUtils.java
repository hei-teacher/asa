package school.hei.asa.endpoint.rest.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import org.springframework.util.SerializationUtils;

public class CookieUtils {

  public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
    if (request.getCookies() == null) return Optional.empty();
    return Arrays.stream(request.getCookies()).filter(c -> c.getName().equals(name)).findFirst();
  }

  public static void addCookie(
      HttpServletResponse response, String name, String value, int maxAge) {
    Cookie cookie = new Cookie(name, value);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setMaxAge(maxAge);
    response.addCookie(cookie);
  }

  public static void deleteCookie(
      HttpServletRequest request, HttpServletResponse response, String name) {
    getCookie(request, name)
        .ifPresent(
            cookie -> {
              cookie.setMaxAge(0);
              cookie.setValue("");
              cookie.setPath("/");
              response.addCookie(cookie);
            });
  }

  public static String serialize(Object object) {
    return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
  }

  public static <T> T deserialize(Cookie cookie, Class<T> cls) {
    return cls.cast(
        SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));
  }
}
