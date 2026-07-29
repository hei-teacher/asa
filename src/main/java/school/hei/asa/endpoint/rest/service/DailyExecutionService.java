package school.hei.asa.endpoint.rest.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.rest.controller.mapper.ThDailyExecutionFormMapper;
import school.hei.asa.endpoint.rest.model.th.ThDailyExecutionForm;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.DailyExecutionRepository;
import school.hei.asa.service.ContractAlertService;
import school.hei.asa.service.ContractExhaustedException;
import school.hei.asa.service.ContractService;

@Service
@AllArgsConstructor
public class DailyExecutionService {
  private final DailyExecutionRepository dailyExecutionRepository;
  private final ThDailyExecutionFormMapper thDailyExecutionFormMapper;
  private final ContractAlertService contractAlertService;
  private final ContractService contractService;

  public void saveAndAlert(ThDailyExecutionForm dmeForm, Worker worker) {
    if (contractService.getRemainingDaysByWorker(worker) <= 0) {
      throw new ContractExhaustedException(
          "Cannot submit report: " + worker.name() + " has no remaining contract days.");
    }
    dailyExecutionRepository.save(thDailyExecutionFormMapper.toDomain(dmeForm, worker));
    contractAlertService.sendContractAlert(worker);
  }
}
