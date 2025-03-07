package school.hei.asa.service;

import static java.util.stream.Collectors.toMap;

import java.time.Month;
import java.util.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.rest.controller.mapper.ThProductMapper;
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
    EnumMap<Month, List<ThProduct>> thProductsByMonth = new EnumMap<>(Month.class);
    Map<String, List<ThProduct>> res = new LinkedHashMap<>();
    EnumSet.allOf(Month.class)
        .forEach(
            month -> {
              List<ThProduct> filteredProducts =
                  thProducts.stream()
                      .map(p -> p.filterByMonth(month))
                      .filter(Objects::nonNull)
                      .toList();
              thProductsByMonth.put(month, filteredProducts);
            });

    EnumSet.allOf(Month.class)
        .forEach(
            (month) -> {
              var monthProducts = thProductsByMonth.getOrDefault(month, List.of());
              if (monthProducts.stream().mapToDouble(ThProduct::executedDays).sum() > 0) {
                res.putIfAbsent(month.toString().toLowerCase(), monthProducts);
              }
            });
    return res;
  }

  public Map<String, Double> thProductsExecutedDaysSumByMonth(List<ThProduct> thProducts) {
    return Arrays.stream(Month.values())
        .collect(
            toMap(
                month ->
                    thProductsExecutedDaysSum(thProducts, month) > 0
                        ? month.toString().toLowerCase()
                        : "",
                month -> {
                  double sum = thProductsExecutedDaysSum(thProducts, month);
                  return sum > 0 ? sum : 0;
                },
                (v1, v2) -> v1,
                LinkedHashMap::new));
  }

  public Double thProductsExecutedDaysSum(List<ThProduct> thProducts, Month month) {
    return thProducts.stream()
        .map(p -> p.filterByMonth(month))
        .mapToDouble(ThProduct::executedDays)
        .sum();
  }
}
