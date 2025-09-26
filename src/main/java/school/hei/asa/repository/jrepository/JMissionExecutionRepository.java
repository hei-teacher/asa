package school.hei.asa.repository.jrepository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import school.hei.asa.repository.model.JMissionExecution;
import school.hei.asa.repository.model.JWorker;
import school.hei.asa.repository.model.WorkerDayPercentageSummary;

@Repository
public interface JMissionExecutionRepository extends JpaRepository<JMissionExecution, String> {
  @Override
  List<JMissionExecution> findAll();

  List<JMissionExecution> findAllByWorker(JWorker jWorker);

  List<JMissionExecution> findByDateBetween(LocalDate startDate, LocalDate endDate);

  List<JMissionExecution> findByWorkerCodeAndDateBetween(
      String workerCode, LocalDate startDate, LocalDate endDate);

  @Query(
      value =
          "SELECT me.worker_code as workerCode, "
              + "sum(me.day_percentage) as totalDayPercentage, "
              + "me.creation_instant as creationInstant, "
              + "me.mission_code as missionCode "
              + "FROM mission_execution me "
              + "GROUP BY workerCode, creationInstant, missionCode "
              + "HAVING (creation_instant BETWEEN :startDate AND :endDate) "
              + "AND (worker_code = :workerCode)",
      nativeQuery = true)
  List<WorkerDayPercentageSummary> findWorkerDayPercentageSummary(
      @Param("workerCode") String workerCode,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);
}
