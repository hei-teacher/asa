package school.hei.asa.endpoint.rest.controller;

import static java.time.Month.FEBRUARY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static school.hei.asa.model.contract.cas.ContractToCasTest.JAN1_2026;
import static school.hei.asa.model.contract.cas.ContractToCasTest.studentContract;

import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.repository.ContractRepository;
import school.hei.asa.service.InvoiceService;

class FinancialPlanControllerIT extends FacadeIT {

  @Autowired FinancialPlanController subject;

  @MockBean ContractRepository contractRepository;
  @MockBean InvoiceService invoiceService;

  @Test
  void oneMonth_complete_studentContract() {
    when(contractRepository.findByYear(2026))
        .thenReturn(List.of(studentContract(JAN1_2026, 11, 50_000)));
    when(invoiceService.getInvoiceTotalAmountByMonth(YearMonth.of(2026, FEBRUARY)))
        .thenReturn(2_000L);
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
                      jan: 0.0,
                        feb: -2000.0,
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
                      ]
                      koContracts =
        """
            .trim(),
        cost.trim());
  }
}
