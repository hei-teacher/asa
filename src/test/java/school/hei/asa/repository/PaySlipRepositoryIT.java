package school.hei.asa.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.YearMonth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.jrepository.JPaySlipRepository;
import school.hei.asa.service.InvoiceService;

class PaySlipRepositoryIT extends FacadeIT {
  @Autowired InvoiceService invoiceService;
  @Autowired PaySlipRepository paySlipRepository;
  @Autowired JPaySlipRepository jPaySlipRepository;

  private Worker payslipWorker() {
    return new Worker("W-PAYSLIP-01", "Rina Rakoto", "", "", "", "", "", "", null, null);
  }

  @Test
  void saving_a_payslip_persists_it_linked_to_its_worker() {
    var worker = payslipWorker();
    var paySlip = invoiceService.generatePaySlip(worker, YearMonth.of(2026, 3));

    paySlipRepository.save(paySlip, worker);

    var saved = jPaySlipRepository.findByWorkerCodeAndYearMonth("W-PAYSLIP-01", "2026-03");
    assertTrue(saved.isPresent());
    assertEquals(0, paySlip.netAmount().compareTo(saved.get().getNetAmount()));
    assertEquals(paySlip.paidLeave().getLeft(), saved.get().getLeftPaidLeave());
  }

  @Test
  void saving_the_same_worker_and_month_twice_updates_instead_of_duplicating() {
    var worker = payslipWorker();
    var paySlip = invoiceService.generatePaySlip(worker, YearMonth.of(2026, 4));

    paySlipRepository.save(paySlip, worker);
    paySlipRepository.save(paySlip, worker);

    var all = jPaySlipRepository.findAll();
    var matching =
        all.stream()
            .filter(p -> "W-PAYSLIP-01".equals(p.getWorker().getCode()))
            .filter(p -> "2026-04".equals(p.getYearMonth()))
            .toList();
    assertEquals(1, matching.size());
  }
}
