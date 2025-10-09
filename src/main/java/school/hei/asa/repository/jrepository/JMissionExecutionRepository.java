package school.hei.asa.repository.jrepository;

import java.time.Instant;
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
      "SELECT new school.hei.asa.repository.model.WorkerDayPercentageSummary("
          + "me.worker_code, "
          + "SUM(me.dayPercentage), "
          + "me.reportedAt, "
          + "me.mission_code) "
          + "FROM JMissionExecution me "
          + "WHERE me.reportedAt BETWEEN :startDate AND :endDate "
          + "AND me.worker_code = :workerCode "
          + "GROUP BY me.worker_code, me.reportedAt, me.mission_code")
  List<WorkerDayPercentageSummary> findWorkerDayPercentageSummary(
      @Param("workerCode") String workerCode,
      @Param("startDate") Instant startDate,
      @Param("endDate") Instant endDate);
}
