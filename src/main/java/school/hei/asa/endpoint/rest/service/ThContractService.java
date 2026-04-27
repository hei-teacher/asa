package school.hei.asa.endpoint.rest.service;

import static java.lang.Double.parseDouble;
import static java.lang.System.lineSeparator;
import static java.time.LocalDate.now;
import static java.time.format.DateTimeFormatter.ofPattern;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.rest.controller.mapper.ThContractMapper;
import school.hei.asa.endpoint.rest.model.th.ThContract;
import school.hei.asa.model.Worker;
import school.hei.asa.service.ContractService;

@Slf4j
@AllArgsConstructor
@Service
public class ThContractService {
  private final ContractService contractService;
  private final ThContractMapper thContractMapper;
  private final DateTimeFormatter localDateFormatter = ofPattern("dd MMM yyyy");

  public Map<Worker, List<ThContract>> totalWorkDaysPerWorker() {
    var totalWorkDaysPerWorker = contractService.totalWorkDaysPerWorker();
    return thContractMapper.toThContractsByWorker(totalWorkDaysPerWorker);
  }

  public Map<Worker, List<ThContract>> totalWorkDaysForOneWorker(String workerCode) {
    var totalWorkDaysPerWorker = contractService.totalWorkDaysForOneWorker(workerCode);
    return thContractMapper.toThContractsByWorker(totalWorkDaysPerWorker);
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

  private void writeToFile(File file, Map<Worker, List<ThContract>> totalWorkDaysPerWorker) {
    try (FileWriter fileWriter = new FileWriter(file)) {
      fileWriter.write(
          "code,worker,contract level,start date,"
              + "contract duration (in days),"
              + "total days worked,remaining days"
              + lineSeparator());
      fileWriter.flush();
      totalWorkDaysPerWorker.forEach(
          (worker, thContracts) ->
              thContracts.parallelStream()
                  .forEach(
                      thContract -> {
                        try {
                          fileWriter.write(newEntryFrom(thContract, worker));
                          fileWriter.flush();
                        } catch (IOException e) {
                          throw new RuntimeException(e);
                        }
                      }));

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String newEntryFrom(ThContract thContract, Worker worker) {
    String remainingDays =
        remainingDaysToString(thContract).equals("0") ? "-" : remainingDaysToString(thContract);
    String actualWorkedDays = actualWorkedDaysToString(thContract);
    String newEntry =
        String.format(
            "%s,%s,%s,%s,%s,%s,%s",
            worker.code(),
            worker.name(),
            thContract.level(),
            thContract.entranceInstant(),
            thContract.duration(),
            actualWorkedDays,
            remainingDays);
    return newEntry + lineSeparator();
  }

  private String remainingDaysToString(ThContract thContract) {
    if (thContract.duration().equals("-") || thContract.actualWorkedDays().equals("-")) {
      return "-";
    }
    var startDate = LocalDate.parse(thContract.entranceInstant(), localDateFormatter);
    var endDate =
        !thContract.endInstant().equals("-")
            ? LocalDate.parse(thContract.endInstant(), localDateFormatter)
            : now();

    var res =
        parseDouble(thContract.duration()) - parseDouble(actualWorkedDaysToString(thContract));
    return formatDays(res);
  }

  private String actualWorkedDaysToString(ThContract thContract) {
    if (thContract.actualWorkedDays().equals("-")) {
      return "-";
    }
    double res = parseDouble(thContract.actualWorkedDays());
    return res == 0.0d ? "-" : formatDays(res);
  }

  private String formatDays(double days) {
    var decimalFormatSymbols = new DecimalFormatSymbols();
    decimalFormatSymbols.setDecimalSeparator('.');
    var numberFormat = new DecimalFormat("#.0", decimalFormatSymbols);
    return numberFormat.format(days);
  }
}
