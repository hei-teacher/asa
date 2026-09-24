package school.hei.asa.repository;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import school.hei.asa.model.Credit;
import school.hei.asa.repository.jrepository.JCreditRepository;
import school.hei.asa.repository.mapper.CreditMapper;

@AllArgsConstructor
@Repository
public class CreditRepository {

  private final JCreditRepository jCreditRepository;
  private final CreditMapper creditMapper;

  @Transactional
  public Optional<Credit> findById(String id) {
    return jCreditRepository.findById(id).map(creditMapper::toDomain);
  }

  @Transactional
  public List<Credit> findAll() {
    return jCreditRepository.findAll().stream().map(creditMapper::toDomain).toList();
  }
}
