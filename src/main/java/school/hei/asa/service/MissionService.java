package school.hei.asa.service;

import java.time.Month;
import java.util.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.rest.controller.mapper.ThProductMapper;
import school.hei.asa.endpoint.rest.model.th.ThMission;
import school.hei.asa.endpoint.rest.model.th.ThProduct;
import school.hei.asa.repository.ProductRepository;

@AllArgsConstructor
@Service
public class MissionService {

  private final ProductRepository productRepository;
  private final ThProductMapper thProductMapper;

  public List<ThProduct> filterThProductsByWorkerCode(String workerCode) {
    var thProducts = thProductMapper.toTh(productRepository.findAll());
    return workerCode == null || workerCode.isBlank()
        ? thProducts
        : thProducts.stream().map(p -> p.filterByWorkerCode(workerCode)).toList();
  }

  public Map<String, List<ThProduct>> thProductsByMonth(List<ThProduct> thProducts) {
    EnumSet<Month> months = EnumSet.allOf(Month.class);
    Map<String, List<ThProduct>> res = new LinkedHashMap<>();
    months.forEach(
        (month) -> {
          List<ThProduct> monthProducts =
              thProducts.stream()
                  .map(p -> p.filterByMonth(month))
                  .filter(Objects::nonNull)
                  .toList();

          var hasExecutedDays =
              monthProducts.stream().mapToDouble(ThProduct::executedDays).sum() > 0;

          if (hasExecutedDays) {
            res.putIfAbsent(month.toString().toLowerCase(), monthProducts);
          }
        });
    return res;
  }

  public Map<String, Double> thProductsExecutedDaysSumByMonth(List<ThProduct> thProducts) {
    EnumSet<Month> months = EnumSet.allOf(Month.class);
    Map<String, Double> res = new LinkedHashMap<>();
    months.forEach(
        (month) -> {
          List<ThProduct> monthProducts =
              thProducts.stream()
                  .map(p -> p.filterByMonth(month))
                  .filter(Objects::nonNull)
                  .toList();

          var hasExecutedDays =
              monthProducts.stream().mapToDouble(ThProduct::executedDays).sum() > 0;

          if (hasExecutedDays) {
            res.putIfAbsent(
                month.toString().toLowerCase(), thProductsExecutedDaysSum(monthProducts, month));
          }
        });
    return res;
  }

  public Double thProductsExecutedDaysSum(List<ThProduct> thProducts, Month month) {
    return thProducts.stream()
        .map(p -> p.filterByMonth(month))
        .mapToDouble(ThProduct::executedDays)
        .sum();
  }

  public Map<String, Double> thMissionsExecutedDaysSumByMonth(List<ThMission> thMissions) {
    EnumSet<Month> months = EnumSet.allOf(Month.class);
    Map<String, Double> res = new LinkedHashMap<>();
    months.forEach(
        (month) -> {
          List<ThMission> monthMission =
              thMissions.stream()
                  .map(p -> p.filterByMonth(month))
                  .filter(Objects::nonNull)
                  .toList();

          var hasExecutedDays =
              monthMission.stream().mapToDouble(ThMission::executedDays).sum() > 0;

          if (hasExecutedDays) {
            res.merge(
                month.toString().toLowerCase(),
                thMissionsExecutedDaysSum(monthMission, month),
                (a, b) -> a + b);
          }
        });
    return res;
  }

  public Double thMissionsExecutedDaysSum(List<ThMission> thMissions, Month month) {
    return thMissions.stream()
        .map(p -> p.filterByMonth(month))
        .mapToDouble(ThMission::executedDays)
        .sum();
  }

  public List<ThMission> filterThMissionsByWorkerCode(List<ThProduct> thProducts) {
    List<ThMission> missions = new ArrayList<>();
    thProducts.forEach(p -> missions.addAll(p.missions()));
    return missions;
  }
}
