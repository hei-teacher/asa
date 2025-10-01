package school.hei.asa.endpoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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

    String incomingState = request.getParameter("state");
    if (incomingState == null) return null;

    for (String key : cache.keySet()) {
      String decodedKey = URLDecoder.decode(key, StandardCharsets.UTF_8);
      String decodedState = URLDecoder.decode(incomingState, StandardCharsets.UTF_8);
      if (decodedKey.equals(decodedState)) {
        return cache.remove(key);
      }
    }
    return null;
  }
}
