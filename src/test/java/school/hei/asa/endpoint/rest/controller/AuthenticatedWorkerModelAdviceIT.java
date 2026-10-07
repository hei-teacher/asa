package school.hei.asa.endpoint.rest.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.ui.Model;
import school.hei.asa.conf.FacadeITMockedThirdParties;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.model.Worker;

class AuthenticatedWorkerModelAdviceIT extends FacadeITMockedThirdParties {
  @Autowired AuthenticatedWorkerModelAdvice advice;
  @MockBean WorkerFromAuthentication workerFromAuthentication;

  private Model modelFor(String workerCode) {
    var authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(mock(DefaultOidcUser.class));
    var worker = new Worker(workerCode, "Rina Rakoto", "", "", "", "", "", "", null, null);
    when(workerFromAuthentication.apply(authentication)).thenReturn(Optional.of(worker));
    var model = mock(Model.class);
    advice.addAuthenticatedWorkerCode(authentication, model);
    return model;
  }

  @Test
  void full_time_employee_sees_payslip_label() {
    verify(modelFor("W-PAYSLIP-01")).addAttribute("documentLabel", "payslip");
  }

  @Test
  void other_workers_see_invoice_label() {
    verify(modelFor("W-P-2024-01")).addAttribute("documentLabel", "invoice");
  }
}
