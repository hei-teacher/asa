package school.hei.asa.endpoint.rest.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final WorkerFromAuthentication workerFromAuthentication;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {
    var workerOpt = workerFromAuthentication.apply(authentication);
    if (workerOpt.isEmpty()) {
      var principal = (DefaultOidcUser) authentication.getPrincipal();
      throw new RuntimeException(
          "Email does not correspond to a known worker: " + principal.getEmail());
    }

    super.onAuthenticationSuccess(request, response, authentication);
  }
}
