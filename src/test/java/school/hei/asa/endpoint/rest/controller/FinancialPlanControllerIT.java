package school.hei.asa.endpoint.rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static school.hei.asa.model.contract.cas.ContractToCasTest.JAN1_2026;
import static school.hei.asa.model.contract.cas.ContractToCasTest.studentContract;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.model.InvoiceForm;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.ContractRepository;
import school.hei.asa.repository.WorkerRepository;
import school.hei.asa.service.InvoiceService;

class FinancialPlanControllerIT extends FacadeIT {

  @Autowired FinancialPlanController subject;

  @MockBean ContractRepository contractRepository;
  @MockBean InvoiceService invoiceService;
  @MockBean WorkerRepository workerRepository;

  @Test
  void oneMonth_complete_studentContract() {
    var invoiceForm =
        new InvoiceForm(
            UUID.randomUUID().toString(),
            YearMonth.of(2026, 1),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null);
    when(workerRepository.findAll())
        .thenReturn(
            List.of(
                new Worker(
                    "W-101",
                    "Test Worker",
                    "worker@example.com",
                    "Full Worker Name",
                    "address",
                    "random city",
                    "nif",
                    "stat")));

    when(contractRepository.findByYear(2026))
        .thenReturn(List.of(studentContract(JAN1_2026, 11, 50_000)));
    when(invoiceService.extractInvoiceData(any(Worker.class), any(InvoiceForm.class)))
        .thenReturn(
            new InvoiceForm(
                "id",
                YearMonth.of(2026, 1),
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                "job",
                0.0d,
                BigDecimal.valueOf(2000),
                BigDecimal.valueOf(2000),
                false,
                null,
                null,
                null,
                null,
                BigDecimal.valueOf(2000),
                "Zéro",
                "Banque: , Agence: , Compte: , Clé: , IBAN: "));
    var cost = subject.financialPlan(2026);

    assertEquals(
        """
                plannedCost = [
                        jan: 0.0,
                        feb: -550000.0,
                        mar: 0.0,
                        apr: 0.0,
                        may: 0.0,
                        jun: 0.0,
                        jul: 0.0,
                        aug: 0.0,
                        sep: 0.0,
                        oct: 0.0,
                        nov: 0.0,
                        dec: 0.0
                      ],
                      executedCost = [
                      jan: -2000.0,
                        feb: -2000.0,
                        mar: -2000.0,
                        apr: -2000.0,
                        may: -2000.0,
                        jun: -2000.0,
                        jul: -2000.0,
                        aug: -2000.0,
                        sep: -2000.0,
                        oct: -2000.0,
                        nov: -2000.0,
                        dec: -2000.0
                      ]
                      koContracts =
        """
            .trim(),
        cost.trim());
  }
}
