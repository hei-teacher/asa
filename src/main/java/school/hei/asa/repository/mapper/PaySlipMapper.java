package school.hei.asa.repository.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.asa.model.PaySlipForm;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.jrepository.JCreditRepository;
import school.hei.asa.repository.jrepository.JTaxRepository;
import school.hei.asa.repository.model.JPaySlip;

@AllArgsConstructor
@Component
public class PaySlipMapper {
  private final WorkerMapper workerMapper;
  private final JTaxRepository jTaxRepository;
  private final JCreditRepository jCreditRepository;

  public JPaySlip toEntity(PaySlipForm paySlipForm, Worker worker) {
    var jPaySlip = new JPaySlip();
    jPaySlip.setId(worker.code() + "_" + paySlipForm.yearMonth());
    jPaySlip.setWorker(workerMapper.toEntity(worker));
    jPaySlip.setYearMonth(paySlipForm.yearMonth().toString());
    jPaySlip.setGrossAmount(paySlipForm.grossAmount());
    jPaySlip.setNetAmount(paySlipForm.netAmount());
    jPaySlip.setTotalAmount(paySlipForm.deductionAndTaxTotal());
    jPaySlip.setPaidLeaveAmount(paySlipForm.paidLeave().getBase());
    jPaySlip.setTakenPaidLeave(paySlipForm.paidLeave().getTaken());
    jPaySlip.setLeftPaidLeave(paySlipForm.paidLeave().getLeft());

    var taxIds =
        paySlipForm.resolvedTaxes().stream().map(resolvedTax -> resolvedTax.tax().getId()).toList();
    jPaySlip.setTaxes(jTaxRepository.findAllById(taxIds));

    var creditIds =
        paySlipForm.credits().stream()
            .map(credit -> credit.getCredit().getId())
            .distinct()
            .toList();
    jPaySlip.setCredits(jCreditRepository.findAllById(creditIds));

    return jPaySlip;
  }
}
