package school.hei.asa.endpoint.rest.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

public class FixedStateAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

  private final OAuth2AuthorizationRequestResolver defaultResolver;

  public FixedStateAuthorizationRequestResolver(ClientRegistrationRepository repo, String baseUri) {
    this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(repo, baseUri);
  }

  @Override
  public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
    OAuth2AuthorizationRequest req = defaultResolver.resolve(request);
    return forceState(req);
  }

  @Override
  public OAuth2AuthorizationRequest resolve(
      HttpServletRequest request, String clientRegistrationId) {
    OAuth2AuthorizationRequest req = defaultResolver.resolve(request, clientRegistrationId);
    return forceState(req);
  }

  private OAuth2AuthorizationRequest forceState(OAuth2AuthorizationRequest req) {
    if (req == null) return null;
    return OAuth2AuthorizationRequest.from(req)
        .state("TNiNyTAu6cQlR-M7DAww-kcao-nxT_7gH3kNdX4Toys%3D")
        .build();
  }
}
