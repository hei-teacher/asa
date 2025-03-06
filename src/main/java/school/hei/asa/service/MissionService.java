package school.hei.asa.service;

import static java.util.stream.Collectors.toMap;

import java.time.Month;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    return Arrays.stream(Month.values())
        .collect(
            toMap(
                month ->
                        thProductsExecutedDaysSum(thProducts, month) > 0
                        ? month.toString().toLowerCase()
                        : " ",
                month ->
                        thProductsExecutedDaysSum(thProducts, month) > 0
                        ? thProducts.stream().map(p -> p.filterByMonth(month)).toList()
                        : List.of(),
                (v1, v2) -> v1,
                LinkedHashMap::new));
  }

  public Map<String, Double> thProductsExecutedDaysSumByMonth(List<ThProduct> thProducts) {
    return Arrays.stream(Month.values())
            .collect(
                    toMap(
                            month ->
                                    thProductsExecutedDaysSum(thProducts, month) > 0
                                            ? month.toString().toLowerCase()
                                            : " ",
                            month ->
                                    thProductsExecutedDaysSum(thProducts, month) > 0
                                            ? thProductsExecutedDaysSum(thProducts, month)
                                            : 0,
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
