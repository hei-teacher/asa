package school.hei.asa.endpoint.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;

@ControllerAdvice
@AllArgsConstructor
public class AuthenticatedWorkerModelAdvice {

  private final WorkerFromAuthentication workerFromAuthentication;

  @ModelAttribute
  public void addAuthenticatedWorkerCode(Authentication authentication, Model model) {
    if (authentication != null && authentication.getPrincipal() instanceof DefaultOidcUser) {
      workerFromAuthentication
          .apply(authentication)
          .ifPresent(worker -> model.addAttribute("authenticatedWorkerCode", worker.code()));
    }
  }
}
