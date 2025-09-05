package school.hei.asa.service;

import java.time.Month;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.rest.controller.mapper.ThProductMapper;
import school.hei.asa.endpoint.rest.model.th.ThMission;
import school.hei.asa.endpoint.rest.model.th.ThProduct;
import school.hei.asa.repository.ProductRepository;

import static java.util.Comparator.naturalOrder;

@Slf4j
@AllArgsConstructor
@Service
public class MissionService {

  private final ProductRepository productRepository;
  private final ThProductMapper thProductMapper;

  public List<ThProduct> filterThProductsByWorkerCode(String workerCode) {
    var thProducts = thProductMapper.toTh(productRepository.findAll());
    return workerCode == null || workerCode.isBlank()
        ? thProducts.stream().sorted(Comparator.comparing(ThProduct::executedDays,naturalOrder()).reversed()).toList()
        : thProducts.stream().map(p -> p.filterByWorkerCode(workerCode)).sorted(Comparator.comparing(ThProduct::executedDays,naturalOrder()).reversed()).toList();
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

  public List<ThMission> filterThMissionsByWorkerCode(List<ThProduct> thProducts) {
    List<ThMission> missions = new ArrayList<>();
    thProducts.forEach(p -> missions.addAll(p.missions()));
      return missions.stream().sorted(Comparator.comparing(ThMission::executedDays,naturalOrder()).reversed()).toList();
  }

    public List<ThMission> thMissionsByWorkerCode(List<ThProduct> thProducts) {
        List<ThMission> missions = new ArrayList<>();

        thProducts.forEach(p ->{
            p.missions().forEach(m ->{
                var filteredMission = missions.stream().filter(thMission -> thMission.getTitle().equals(m.getTitle())).toList();
                if (filteredMission.isEmpty()){
                    ThMission newMission = new ThMission(
                            m.getCode().substring(3),
                            m.getTitle(),
                            m.getDescription(),
                            m.getMissionExecutions(),
                            m.isCare()
                    );
                    missions.add(newMission);
                }
                else {
                    try {
                        var mission = filteredMission.getFirst();
                        var index = missions.indexOf(mission);
                        var missionExecutions = new ArrayList<>(mission.getMissionExecutions());
                        missionExecutions.addAll(m.getMissionExecutions());
                        mission.setMissionExecutions(missionExecutions);
                        missions.set(index, mission);
                    } catch (Exception e) {
                        log.error("here is the error: {}", e.toString());
                    }
                }
            });
        });
        return missions.stream().sorted(Comparator.comparing(ThMission::executedDays,naturalOrder()).reversed()).toList();
    }
}
