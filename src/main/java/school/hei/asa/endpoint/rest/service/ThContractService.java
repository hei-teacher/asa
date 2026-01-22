package school.hei.asa.endpoint.rest.service;

import static java.lang.Double.parseDouble;
import static java.lang.System.lineSeparator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.rest.controller.mapper.ThContractMapper;
import school.hei.asa.endpoint.rest.model.th.ThContract;
import school.hei.asa.model.Worker;
import school.hei.asa.service.ContractService;

@AllArgsConstructor
@Service
public class ThContractService {
  private final ContractService contractService;
  private final ThContractMapper thContractMapper;

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
    String remainingDays = remainingDaysToString(thContract);
    String actualWorkedDays = actualWorkedDaysToString(thContract);
    String startDate =
        thContract.entranceInstant().atZone(ZoneId.of("UTC")).toLocalDate().toString();
    String newEntry =
        String.format(
            "%s,%s,%s,%s,%s,%s,%s",
            worker.code(),
            worker.name(),
            thContract.level(),
            startDate,
            thContract.duration(),
            actualWorkedDays,
            remainingDays);
    return newEntry + lineSeparator();
  }

  private String remainingDaysToString(ThContract thContract) {
    if (thContract.duration().equals("-") || thContract.actualWorkedDay().equals("-")) {
      return "-";
    }

    var res = parseDouble(thContract.duration()) - parseDouble(thContract.actualWorkedDay());
    return formatDays(res);
  }

  private String actualWorkedDaysToString(ThContract thContract) {
    if (thContract.actualWorkedDay().equals("-")){
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

    public List<ThContract> getAllContractsForWorker(Worker worker) {
        return thContractMapper.toTh(contractService.getAllContractsForWorker(worker));
    }
}
