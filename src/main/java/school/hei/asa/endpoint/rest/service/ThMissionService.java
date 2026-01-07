package school.hei.asa.endpoint.rest.service;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.hei.asa.endpoint.rest.model.th.ThMission;
import school.hei.asa.endpoint.rest.model.th.ThMissionExecution;
import school.hei.asa.endpoint.rest.model.th.ThProduct;

@Service
@Slf4j
@AllArgsConstructor
public class ThMissionService {
  public List<ThMission> filterThMissionsByDateBetween(
      List<ThMission> missions, LocalDate startDate, LocalDate endDate) {
    return missions.stream()
        .map(
            m -> {
              var missionExecutions =
                  filterThMissionExecutionsByDateBetween(
                      m.getMissionExecutions(), startDate, endDate);
              return new ThMission(
                  m.getCode(),
                  m.getTitle(),
                  m.getDescription(),
                  missionExecutions,
                  m.isCare(),
                  m.isUnpaidCare());
            })
        .toList();
  }

  public List<ThMission> getUniqueMissionsByTitle(List<ThProduct> thProducts) {
    List<ThMission> missions = new ArrayList<>();
    thProducts.forEach(p -> missions.addAll(p.missions()));
    return missions.stream()
        .sorted(comparing(ThMission::executedDays, naturalOrder()).reversed())
        .toList();
  }

  public List<ThMission> getAllMissionsFromProducts(List<ThProduct> thProducts) {
    List<ThMission> result = new ArrayList<>();

    thProducts.forEach(
        p ->
            p.missions()
                .forEach(
                    m -> {
                      var missions = filterThMissionsByTitle(result, m.getTitle());
                      if (missions.isEmpty()) {
                        result.add(
                            new ThMission(
                                m.getCode().substring(3),
                                m.getTitle(),
                                m.getDescription(),
                                m.getMissionExecutions(),
                                m.isCare(),
                                m.isUnpaidCare()));
                      } else {
                        var mission = missions.getFirst();
                        var index = result.indexOf(mission);
                        var missionExecutions = new ArrayList<>(mission.getMissionExecutions());
                        missionExecutions.addAll(m.getMissionExecutions());
                        mission.setMissionExecutions(missionExecutions);
                        result.set(index, mission);
                      }
                    }));
    return result.stream()
        .sorted(comparing(ThMission::executedDays, naturalOrder()).reversed())
        .toList();
  }

  public List<ThMission> filterThMissionsByTitle(List<ThMission> missions, String title) {
    return missions.stream().filter(m -> m.getTitle().equals(title)).toList();
  }

  public List<ThMissionExecution> filterThMissionExecutionsByDateBetween(
      List<ThMissionExecution> missionExecutions, LocalDate startDate, LocalDate endDate) {
    return missionExecutions.stream()
        .filter(
            me -> {
              var isBetween = me.getDate().isAfter(startDate) && me.getDate().isBefore(endDate);
              return isBetween || me.getDate().isEqual(startDate) || me.getDate().isEqual(endDate);
            })
        .toList();
  }
}
