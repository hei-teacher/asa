package school.hei.asa.endpoint.rest.service;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.rest.controller.mapper.ThProductMapper;
import school.hei.asa.endpoint.rest.model.th.ThMission;
import school.hei.asa.endpoint.rest.model.th.ThProduct;
import school.hei.asa.service.ProductService;

@Service
@AllArgsConstructor
public class ThProductService {
  private final ProductService productService;
  private final ThProductMapper thProductMapper;
  private final ThMissionService thMissionService;

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
    return thProducts.stream()
        .map(
            p -> {
              var missions =
                  thMissionService.filterThMissionsByDateBetween(
                      p.missions(), startLocalDate, endLocalDate);
              return new ThProduct(p.code(), p.name(), p.description(), missions, p.isCare());
            })
        .toList();
  }

  public List<ThProduct> filterThProductsByWorkerCode(
      String workerCode, boolean noUnpaidCareMissions) {
    var thProduct = getAllThProducts(noUnpaidCareMissions);
    if (workerCode == null || workerCode.isBlank()) {
      return thProduct;
    }
    return thProduct.stream().map(p -> p.filterByWorkerCode(workerCode)).toList();
  }

  public List<ThProduct> getAllThProducts(boolean noUnpaidCareMissions) {
    var thProduct = thProductMapper.toTh(productService.getAllProducts());
    return thProduct.stream()
        .map(p -> filterUnpaidCareMissions(p, noUnpaidCareMissions))
        .sorted(comparing(ThProduct::executedDays, naturalOrder()).reversed())
        .toList();
  }

  public Map<String, List<ThProduct>> thProductsByMonth(List<ThProduct> thProducts) {
    EnumSet<Month> months = EnumSet.allOf(Month.class);
    Map<String, List<ThProduct>> res = new LinkedHashMap<>();
    months.forEach(
        month -> {
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

  public Map<String, Double> thProductsExecutedDaysSumByMonth(
      List<ThProduct> thProducts, boolean noUnpaidCareMissions) {
    EnumSet<Month> months = EnumSet.allOf(Month.class);
    Map<String, Double> res = new LinkedHashMap<>();
    months.forEach(
        month -> {
          List<ThProduct> monthProducts =
              thProducts.stream()
                  .map(p -> p.filterByMonth(month))
                  .filter(Objects::nonNull)
                  .toList();

          var hasExecutedDays =
              monthProducts.stream().mapToDouble(ThProduct::executedDays).sum() > 0;

          if (hasExecutedDays) {
            res.putIfAbsent(
                month.toString().toLowerCase(),
                thProductsExecutedDaysSum(monthProducts, month, noUnpaidCareMissions));
          }
        });
    return res;
  }

  public Double thProductsExecutedDaysSum(
      List<ThProduct> thProducts, Month month, boolean noUnpaidCareMissions) {
    return thProducts.stream()
        .map(p -> p.filterByMonth(month))
        .flatMap(p -> p.missions().stream())
        .filter(m -> !m.isUnpaidCare() || !noUnpaidCareMissions)
        .mapToDouble(ThMission::executedDays)
        .sum();
  }

  private ThProduct filterUnpaidCareMissions(ThProduct product, boolean noUnpaidCareMissions) {
    return new ThProduct(
        product.code(),
        product.name(),
        product.description(),
        product.missions().stream()
            .filter(m -> !m.isUnpaidCare() || !noUnpaidCareMissions)
            .toList(),
        product.isCare());
  }
}
