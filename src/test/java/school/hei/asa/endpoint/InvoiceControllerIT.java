package school.hei.asa.endpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.rest.controller.InvoiceController;
import school.hei.asa.endpoint.rest.controller.WorkerToModelAdder;
import school.hei.asa.endpoint.rest.model.th.ThInvoiceForm;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.model.PartnerContractor;
import school.hei.asa.model.Worker;

class InvoiceControllerIT extends FacadeIT {

  @Autowired InvoiceController invoiceController;

  @MockBean WorkerFromAuthentication workerFromAuthentication;
  @MockBean WorkerToModelAdder workerToModelAdder;

  Authentication authentication;
  Worker authenticatedWorker;
  Model model;

  @BeforeEach
  void setUp() {
    authentication = mock(Authentication.class);
    authenticatedWorker =
        new PartnerContractor(
            "worker-code",
            "Test Worker",
            "worker@example.com",
            "Full Worker Name",
            "address",
            "random city",
            "nif",
            "stat");
    model = mock(Model.class);

    when(workerFromAuthentication.apply(authentication))
        .thenReturn(Optional.of(authenticatedWorker));
    when(workerToModelAdder.apply(anyString(), any())).thenReturn(authenticatedWorker);
  }

  @Test
  void can_get_invoice() {
    var invoiceForm = new ThInvoiceForm(null, null, "", "", "", "", "", false, "", "", "", "", "");
    String viewName = invoiceController.getInvoicePage(model, invoiceForm);

    assertEquals("invoice-generator", viewName);
  }
}
