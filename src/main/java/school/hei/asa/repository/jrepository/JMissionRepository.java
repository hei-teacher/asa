package school.hei.asa.repository.jrepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.hei.asa.repository.model.JMission;

@Repository
public interface JMissionRepository extends JpaRepository<JMission, String> {
  @Override
  List<JMission> findAll();

  Optional<JMission> findByCode(String code);
}
