package school.hei.asa.repository;

import static java.sql.Date.valueOf;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import school.hei.asa.repository.model.JMissionExecution;

@Repository
public class PsMissionExecutionRepository {

  private final JdbcTemplate jdbcTemplate;

  public PsMissionExecutionRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public void saveAll(List<JMissionExecution> executions) {

    String sql =
        "INSERT INTO mission_execution (id, mission_code, worker_code, date, day_percentage,"
            + " creation_instant, comment) VALUES (?, ?, ?, ?, ?, ?, ?)";

    jdbcTemplate.batchUpdate(
        sql,
        new BatchPreparedStatementSetter() {
          @Override
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            JMissionExecution execution = executions.get(i);
            ps.setString(1, execution.getId());
            ps.setString(2, execution.getMission().getCode());
            ps.setString(3, execution.getWorker().getCode());
            ps.setDate(4, valueOf(execution.getDate().toLocalDate()));
            ps.setDouble(5, execution.getDayPercentage());
            ps.setTimestamp(6, Timestamp.from(execution.getReportedAt()));
            ps.setString(7, execution.getComment());
          }

          @Override
          public int getBatchSize() {
            return executions.size();
          }
        });
  }
}
