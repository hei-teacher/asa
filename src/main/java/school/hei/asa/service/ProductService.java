package school.hei.asa.service;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.rest.controller.mapper.ThProductMapper;
import school.hei.asa.endpoint.rest.model.th.ThMission;
import school.hei.asa.endpoint.rest.model.th.ThMissionExecution;
import school.hei.asa.endpoint.rest.model.th.ThProduct;
import school.hei.asa.repository.ProductRepository;

@Slf4j
@Service
@AllArgsConstructor
public class ProductService {
  private final ThProductMapper thProductMapper;
  private final ProductRepository productRepository;

  private List<ThProduct> filterThProductsByWorkerCode(
      String workerCode, boolean noUnpaidCareMissions) {
    var thProducts =
        thProductMapper.toTh(productRepository.findAll()).stream()
            .map(
                p ->
                    new ThProduct(
                        p.code(),
                        p.name(),
                        p.description(),
                        p.missions().stream()
                            .filter(m -> !m.isUnpaidCare() || !noUnpaidCareMissions)
                            .toList(),
                        p.isCare()))
            .toList();
    return workerCode == null || workerCode.isBlank()
        ? thProducts.stream()
            .sorted(comparing(ThProduct::executedDays, naturalOrder()).reversed())
            .toList()
        : thProducts.stream()
            .map(p -> p.filterByWorkerCode(workerCode))
            .sorted(comparing(ThProduct::executedDays, naturalOrder()).reversed())
            .toList();
  }

  public List<ThProduct> filterThProductByWorkerCodeAndDateBetween(
      String workerCode, String startDate, String endDate, boolean noUnpaidCareMissions) {
    var thProducts = filterThProductsByWorkerCode(workerCode, noUnpaidCareMissions);
    if (startDate == null || startDate.isBlank() || endDate == null || endDate.isBlank()) {
      return thProducts;
    }
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    var startLocalDate = LocalDate.parse(startDate, formatter);
    var endLocalDate = LocalDate.parse(endDate, formatter);
    if (endLocalDate.isBefore(startLocalDate)) {
      return thProducts;
    }
    log.info("filtering by date...");

    List<ThProduct> result = new ArrayList<>();
    thProducts.forEach(
        p -> {
          var missions = p.missions();
          List<ThMission> newMissions = new ArrayList<>();
          missions.forEach(
              m -> {
                List<ThMissionExecution> newMissionExecution =
                    m.getMissionExecutions().stream()
                        .filter(
                            me -> {
                              var isBetween =
                                  me.getDate().isAfter(startLocalDate)
                                      && me.getDate().isBefore(endLocalDate);
                              return isBetween
                                  || me.getDate().isEqual(startLocalDate)
                                  || me.getDate().isEqual(endLocalDate);
                            })
                        .toList();
                newMissions.add(
                    new ThMission(
                        m.getCode(),
                        m.getTitle(),
                        m.getDescription(),
                        newMissionExecution,
                        m.isCare(),
                        m.isUnpaidCare()));
              });
          result.add(new ThProduct(p.code(), p.name(), p.description(), newMissions, p.isCare()));
        });
    if (workerCode.isBlank() || workerCode == null) {
      return result.stream()
          .sorted(comparing(ThProduct::executedDays, naturalOrder()).reversed())
          .toList();
    }
    return result.stream()
        .map(p -> p.filterByWorkerCode(workerCode))
        .sorted(comparing(ThProduct::executedDays, naturalOrder()).reversed())
        .toList();
  }
}
