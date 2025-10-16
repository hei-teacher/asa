package school.hei.asa.repository;

import static java.util.stream.Collectors.groupingBy;
import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import school.hei.asa.model.DailyExecution;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.repository.jrepository.JMissionExecutionRepository;
import school.hei.asa.repository.jrepository.JMissionRepository;
import school.hei.asa.repository.jrepository.JWorkerRepository;
import school.hei.asa.repository.mapper.MissionExecutionMapper;
import school.hei.asa.repository.model.JMissionExecution;

@AllArgsConstructor
@Repository
public class DailyExecutionRepository {

  private final MissionExecutionRepository missionExecutionRepository;
  private final JMissionExecutionRepository jMissionExecutionRepository;
  private final JWorkerRepository jWorkerRepository;
  private final JMissionRepository jMissionRepository;
  private final PsMissionExecutionRepository psMissionExecutionRepository;

  private final MissionExecutionMapper missionExecutionMapper;

  @Transactional(isolation = SERIALIZABLE)
  public void save(DailyExecution dailyExecution) {
    var date = dailyExecution.date();
    if (!missionExecutionRepository.findAllBy(dailyExecution.worker(), date).isEmpty()) {
      throw new IllegalArgumentException("Day already has MissionExecution: " + date);
    }

    var toSave = new ArrayList<JMissionExecution>();
    dailyExecution
        .executions()
        .forEach(missionExecution -> toSave.add(missionExecutionMapper.toEntity(missionExecution)));
    psMissionExecutionRepository.saveAll(toSave);
  }

  @Transactional
  public List<DailyExecution> findAll() {
    var jWorkers = jWorkerRepository.findAll();
    var jMissions = jMissionRepository.findAll();
    var jmeList = jMissionExecutionRepository.findAll();
    var meList = missionExecutionMapper.toDomain(jmeList, jWorkers, jMissions);
    var meListByDate = meList.stream().collect(groupingBy(MissionExecution::date));
    List<DailyExecution> dailyExecutions = new ArrayList<>();
    meListByDate.forEach(
        (date, meListOfDate) -> addToDailyExecutions(date, meListOfDate, dailyExecutions));
    return dailyExecutions;
  }

  @Transactional
  public List<DailyExecution> findByDateBetween(LocalDate startDate, LocalDate endDate) {
    var jWorkers = jWorkerRepository.findAll();
    var jMissions = jMissionRepository.findAll();
    var jmeList = jMissionExecutionRepository.findByDateBetween(startDate, endDate);
    var meList = missionExecutionMapper.toDomain(jmeList, jWorkers, jMissions);
    return groupExecutionsByDate(meList);
  }

  @Transactional
  public List<DailyExecution> findByWorkerCodeAndDateBetween(
      String workerCode, LocalDate startDate, LocalDate endDate) {
    var jWorkers = jWorkerRepository.findAll();
    var jMissions = jMissionRepository.findAll();
    var jmeList =
        jMissionExecutionRepository.findByWorkerCodeAndDateBetween(workerCode, startDate, endDate);
    var meList = missionExecutionMapper.toDomain(jmeList, jWorkers, jMissions);
    return groupExecutionsByDate(meList);
  }

  private List<DailyExecution> groupExecutionsByDate(List<MissionExecution> meList) {
    var meListByDate = meList.stream().collect(Collectors.groupingBy(MissionExecution::date));
    List<DailyExecution> dailyExecutions = new ArrayList<>();
    meListByDate.forEach(
        (date, meListOfDate) -> addToDailyExecutions(date, meListOfDate, dailyExecutions));
    return dailyExecutions;
  }

  private static void addToDailyExecutions(
      LocalDate date, List<MissionExecution> meList, List<DailyExecution> dailyExecutions) {
    var meByWorker = meList.stream().collect(groupingBy(MissionExecution::worker));
    meByWorker.forEach(
        (worker, meListOfWorker) ->
            dailyExecutions.add(new DailyExecution(worker, date, meListOfWorker)));
  }
}
