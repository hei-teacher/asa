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
import school.hei.asa.repository.WorkerRepository;

@AllArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final WorkerRepository workerRepository;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {
    var principal = (DefaultOidcUser) authentication.getPrincipal();
    var email = principal.getEmail();
    var workerOpt = workerRepository.findByEmail(email);
    if (workerOpt.isEmpty()) {
      throw new RuntimeException("Email does not correspond to a known worker: " + email);
    }

    super.onAuthenticationSuccess(request, response, authentication);
  }
}
