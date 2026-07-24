package school.hei.asa.endpoint.rest.controller.mapper;

import static java.time.ZoneId.systemDefault;
import static java.util.Locale.ENGLISH;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.hei.asa.endpoint.rest.model.th.ThContract;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.service.ContractService;

@Slf4j
@AllArgsConstructor
@Component
public class ThContractMapper {
  private final ThWorkerMapper thWorkerMapper;
  private final ContractService contractService;

  public List<ThContract> toTh(List<Contract> contracts) {
    log.info("mapping contracts to Th...");
    List<ThContract> result = new ArrayList<>();
    contracts.forEach(
        current -> {
          log.info("mapping {} for {}", current.level().code(), current.worker().name());
          var contractLevel = current.level();
          var contractType = thWorkerMapper.toWorkerType(contractLevel.type().name());
          var compensation =
              switch (contractLevel.type()) {
                case partnerContractor, studentContractor -> contractLevel.dailyPay();
                case fullTimeEmployee -> contractLevel.monthlyPay();
              };
          var dateFormater = DateTimeFormatter.ofPattern("dd MMM yyyy", ENGLISH);
          log.info("entrance date = {}", current.entranceInstant());
          var entranceDate =
              dateFormater.format(current.entranceInstant().atZone(systemDefault()).toLocalDate());
          var endDate =
              current.endInstant() == null
                  ? "-"
                  : dateFormater.format(current.endInstant().atZone(systemDefault()).toLocalDate());
          var startDate = LocalDate.parse(entranceDate, dateFormater);
          var localEndDate =
              !endDate.equals("-") ? LocalDate.parse(endDate, dateFormater) : LocalDate.now();
          result.add(
              new ThContract(
                  contractLevel.code(),
                  entranceDate,
                  endDate,
                  contractType,
                  BigDecimal.valueOf(compensation),
                  current.company(),
                  current.jobTitle(),
                  current.duration() == null ? "-" : current.duration().toDays() + "",
                  current.contractBucketKey(),
                  contractService.getActualWorkedDaysByDateByWorker(
                      startDate, current.worker().code(), localEndDate)));
        });
    log.info("Successfully mapping contracts to Th !");
    return result;
  }

  public Map<Worker, List<ThContract>> toThContractsByWorker(
      Map<Worker, List<Contract>> contractsByWorker) {
    Map<Worker, List<ThContract>> result = new HashMap<>();
    contractsByWorker.forEach(
        (worker, contracts) -> {
          var thContracts = toTh(contracts);
          result.put(worker, thContracts);
        });
    return result;
  }
}
