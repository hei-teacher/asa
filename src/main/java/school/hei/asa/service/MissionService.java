package school.hei.asa.service;

import static java.lang.Double.parseDouble;
import static java.lang.System.lineSeparator;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.rest.controller.mapper.ThContractMapper;
import school.hei.asa.endpoint.rest.model.th.ThContract;
import school.hei.asa.endpoint.rest.model.th.ThMission;
import school.hei.asa.endpoint.rest.model.th.ThProduct;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.ContractRepository;
import school.hei.asa.repository.WorkerRepository;

@Slf4j
@AllArgsConstructor
@Service
public class MissionService {

  private final WorkerRepository workerRepository;
  private final ContractRepository contractRepository;
  private final ThWorkerMapper thWorkerMapper;

  public Map<Worker, List<ThContract>> totalWorkDaysForOneWorker(String workerCode) {
    Map<Worker, List<ThContract>> result = new HashMap<>();
    var worker = workerRepository.findByCode(workerCode);
    var contracts = thContractMapper.toTh(contractRepository.findAllByWorker(worker));
    result.put(worker, contracts);
    log.info("result be like = {}", result);
    return result;
  }

  public Map<Worker, List<ThContract>> totalWorkDaysPerWorker() {
    Map<Worker, List<ThContract>> result = new HashMap<>();
    var workers = workerRepository.findAll().stream().sorted(comparing(Worker::name)).toList();
    workers.parallelStream()
        .forEach(
            worker -> {
              var contracts = thContractMapper.toTh(contractRepository.findAllByWorker(worker));
              result.put(worker, contracts);
            });
    return result;
  }

  public File generateCSV(String workerCode) {
    var totalWorkDaysPerWorker =
        workerCode == null || workerCode.isBlank()
            ? totalWorkDaysPerWorker()
            : totalWorkDaysForOneWorker(workerCode);
    String filePath = System.getProperty("java.io.tmpdir");
    String fileName =
        workerCode == null || workerCode.isBlank()
            ? "total_work_days-All.csv"
            : "total_work_days-"
                + totalWorkDaysPerWorker.keySet().stream().findFirst().get().name()
                + ".csv";
    File file = new File(filePath, fileName);
    writeToFile(file, totalWorkDaysPerWorker);
    return file;
  }

  @SneakyThrows
  private void writeToFile(File file, Map<Worker, List<ThContract>> totalWorkDaysPerWorker) {
    try (FileWriter fileWriter = new FileWriter(file)) {
      fileWriter.write(
          "code,worker,contract level,start date,"
              + "contract duration (in days),"
              + "total days worked,remaining days"
              + lineSeparator());
      fileWriter.flush();
      totalWorkDaysPerWorker.forEach(
          (worker, thContracts) -> {
            thContracts.parallelStream()
                .forEach(
                    thContract -> {
                      try {
                        String remainingDays = remainingDaysToString(thContract);
                        String actualWorkedDays = actualWorkedDaysToString(thContract);
                        String startDate =
                            thContract
                                .entranceInstant()
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                                .toString();
                        String textToWrite =
                            String.format(
                                "%s,%s,%s,%s,%s,%s,%s",
                                worker.code(),
                                worker.name(),
                                thContract.level(),
                                startDate,
                                thContract.duration(),
                                actualWorkedDays,
                                remainingDays);

                        fileWriter.write(textToWrite + lineSeparator());
                        fileWriter.flush();
                      } catch (IOException e) {
                        throw new RuntimeException(e);
                      }
                    });
          });

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String remainingDaysToString(ThContract thContract) {
    if (thContract.duration().equals("-") || thContract.actualWorkedDay().equals("-")) {
      return "-";
    }

    var res = parseDouble(thContract.duration()) - parseDouble(thContract.actualWorkedDay());
    return formatDays(res);
  }

  private String actualWorkedDaysToString(ThContract thContract) {
    if (thContract.actualWorkedDay().equals("-")) {
      return "-";
    }
    double res = parseDouble(thContract.actualWorkedDay());
    return res == 0.0d ? "-" : formatDays(res);
  }

  private String formatDays(double days) {
    var decimalFormatSymbols = new DecimalFormatSymbols();
    decimalFormatSymbols.setDecimalSeparator('.');
    var numberFormat = new DecimalFormat("#.0", decimalFormatSymbols);
    return numberFormat.format(days);
  }
}
