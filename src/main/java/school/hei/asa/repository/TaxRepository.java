package school.hei.asa.repository;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import school.hei.asa.model.Tax;
import school.hei.asa.repository.jrepository.JTaxRepository;
import school.hei.asa.repository.mapper.TaxMapper;

@AllArgsConstructor
@Repository
public class TaxRepository {

  private final JTaxRepository jTaxRepository;
  private final TaxMapper taxMapper;

  @Transactional
  public Optional<Tax> findById(String id) {
    return jTaxRepository.findById(id).map(taxMapper::toDomain);
  }

  @Transactional
  public List<Tax> findAll() {
    return jTaxRepository.findAll().stream().map(taxMapper::toDomain).toList();
  }
}
