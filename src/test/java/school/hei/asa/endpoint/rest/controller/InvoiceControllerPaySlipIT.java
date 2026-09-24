package school.hei.asa.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

import java.io.File;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import school.hei.asa.conf.FacadeITMockedThirdParties;
import school.hei.asa.endpoint.rest.model.th.ThInvoiceForm;
import school.hei.asa.endpoint.rest.model.th.WorkerModelAdderParam;
import school.hei.asa.endpoint.rest.security.WorkerFromAuthentication;
import school.hei.asa.file.bucket.BucketComponent;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.jrepository.JPaySlipRepository;

class InvoiceControllerPaySlipIT extends FacadeITMockedThirdParties {
  @Autowired InvoiceController invoiceController;
  @Autowired JPaySlipRepository jPaySlipRepository;
  @Autowired BucketComponent bucketComponent;

  @MockBean WorkerFromAuthentication workerFromAuthentication;
  @MockBean WorkerToModelAdder workerToModelAdder;

  private Worker payslipWorker() {
    return new Worker("W-PAYSLIP-01", "Rina Rakoto", "", "", "", "", "", "", null, null);
  }

  @Test
  void generating_a_payslip_saves_it_to_the_database() throws Exception {
    var worker = payslipWorker();
    var authentication = mock(Authentication.class);
    var model = mock(Model.class);
    when(workerFromAuthentication.apply(authentication)).thenReturn(Optional.of(worker));
    when(workerToModelAdder.apply(any(WorkerModelAdderParam.class), any())).thenReturn(worker);

    var invoiceForm =
        new ThInvoiceForm(
            null, "2026-06", null, null, "", "", "", "", false, "", "", "", "", "", "", "");

    var response = invoiceController.generateInvoice(model, authentication, invoiceForm);

    assertEquals(OK, response.getStatusCode());
    assertTrue(response.getHeaders().getContentDisposition().getFilename().startsWith("FDP_"));

    var saved = jPaySlipRepository.findByWorkerCodeAndYearMonth("W-PAYSLIP-01", "2026-06");
    assertTrue(saved.isPresent());

    verify(bucketComponent).upload(any(File.class), anyString());
  }
}
