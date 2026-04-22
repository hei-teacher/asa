package school.hei.asa.repository.jrepository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.hei.asa.repository.model.JProduct;

public interface JProductRepository extends JpaRepository<JProduct, String> {
  @Query(
      "SELECT DISTINCT p FROM JProduct p "
          + "LEFT JOIN FETCH p.missions m "
          + "WHERE EXISTS (SELECT 1 FROM JMissionExecution me "
          + "              WHERE me.mission.product = p "
          + "              AND me.date BETWEEN :startDate AND :endDate )")
  List<JProduct> findAllDeepByDateRange(
      @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

  @Override
  List<JProduct> findAll();

  JProduct findByCode(String code);
}
