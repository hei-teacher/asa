package school.hei.asa.endpoint.rest.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static school.hei.asa.model.contract.ContractType.fullTimeEmployee;
import static school.hei.asa.model.contract.ContractType.studentContractor;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.event.EventProducer;
import school.hei.asa.endpoint.event.model.NewInvoiceGenerated;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.model.contract.ContractLevel;
import school.hei.asa.repository.ContractRepository;
import school.hei.asa.service.InvoiceService;

public class ThInvoiceServiceIT extends FacadeIT {
  @Autowired ThInvoiceService thInvoiceService;
  @MockBean EventProducer<NewInvoiceGenerated> eventProducer;
  @MockBean ContractRepository contractRepository;
  @Autowired private InvoiceService invoiceService;

  @Test
  void send_invoice_copy_ok() throws IOException {
    var event = mock(NewInvoiceGenerated.class);
    doNothing().when(eventProducer).accept(List.of(event));
    invoiceService.sendGenerateInvoiceEvent("invoiceId");
    verify(eventProducer).accept(anyList());
  }

  @Test
  void resolveTemplateName_returns_payslip_when_contract_is_fullTimeEmployee() {
    var worker = newWorker();
    when(contractRepository.findActiveContractByWorker(any()))
        .thenReturn(Optional.of(contractWithType(worker, fullTimeEmployee)));

    var actual = thInvoiceService.resolveTemplateName(worker);

    assertEquals("pay-slip", actual);
  }

  @Test
  void resolveTemplateName_returns_invoice_when_contract_is_not_fullTimeEmployee() {
    var worker = newWorker();
    when(contractRepository.findActiveContractByWorker(any()))
        .thenReturn(Optional.of(contractWithType(worker, studentContractor)));

    var actual = thInvoiceService.resolveTemplateName(worker);

    assertEquals("invoice", actual);
  }

  @Test
  void resolveTemplateName_returns_invoice_when_no_active_contract() {
    var worker = newWorker();
    when(contractRepository.findActiveContractByWorker(any())).thenReturn(Optional.empty());

    var actual = thInvoiceService.resolveTemplateName(worker);

    assertEquals("invoice", actual);
  }

  private Worker newWorker() {
    return new Worker("w-code", "name", "email", "fullname", "address", "city", "nif", "stat");
  }

  private Contract contractWithType(
      Worker worker, school.hei.asa.model.contract.ContractType type) {
    return new Contract(
        worker,
        "job",
        new ContractLevel("code", type, null, 55_556d),
        Instant.now(),
        null,
        Duration.ofDays(100),
        "company",
        "");
  }
}
