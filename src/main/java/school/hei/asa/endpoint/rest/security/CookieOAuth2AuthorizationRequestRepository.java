package school.hei.asa.endpoint.rest.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

@Slf4j
public class CookieOAuth2AuthorizationRequestRepository
    implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

  private static final String COOKIE_NAME = "OAUTH2_AUTH_REQUEST";
  private static final int MAX_AGE = 180;

  @Override
  public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
    log.info(
        "Read authCookie="
            + CookieUtils.getCookie(request, COOKIE_NAME)
                .map(cookie -> cookie.getValue())
                .orElse(""));
    var oAuth2AuthorizationRequest =
        CookieUtils.getCookie(request, COOKIE_NAME)
            .map(cookie -> CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest.class))
            .orElse(null);
    return oAuth2AuthorizationRequest;
  }

  @Override
  public void saveAuthorizationRequest(
      OAuth2AuthorizationRequest authorizationRequest,
      HttpServletRequest request,
      HttpServletResponse response) {
    if (authorizationRequest == null) {
      CookieUtils.deleteCookie(request, response, COOKIE_NAME);
      return;
    }

    log.info("To-save authCookie=" + CookieUtils.serialize(authorizationRequest));
    CookieUtils.addCookie(
        response, COOKIE_NAME, CookieUtils.serialize(authorizationRequest), MAX_AGE);
  }

  @Override
  public OAuth2AuthorizationRequest removeAuthorizationRequest(
      HttpServletRequest request, HttpServletResponse response) {
    return loadAuthorizationRequest(request);
  }
}
