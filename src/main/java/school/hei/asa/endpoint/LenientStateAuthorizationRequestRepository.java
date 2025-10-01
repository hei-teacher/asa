package school.hei.asa.endpoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

public class LenientStateAuthorizationRequestRepository
    implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

  private final Map<String, OAuth2AuthorizationRequest> cache = new HashMap<>();

  @Override
  public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
    String state = request.getParameter("state");
    if (state == null) return null;

    return cache.get(state);
  }

  @Override
  public void saveAuthorizationRequest(
      OAuth2AuthorizationRequest authorizationRequest,
      HttpServletRequest request,
      HttpServletResponse response) {
    if (authorizationRequest == null) return;
    cache.put(authorizationRequest.getState(), authorizationRequest);
  }

  @Override
  public OAuth2AuthorizationRequest removeAuthorizationRequest(
      HttpServletRequest request, HttpServletResponse response) {
    String state = request.getParameter("state");
    if (state == null) return null;

    String matchedKey =
        cache.keySet().stream()
            .filter(key -> state.startsWith(key) || key.startsWith(state))
            .findFirst()
            .orElse(null);

    if (matchedKey != null) {
      return cache.remove(matchedKey);
    }

    return null;
  }
}
