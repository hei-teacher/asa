package school.hei.asa.repository;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import school.hei.asa.model.Worker;
import school.hei.asa.model.contract.Contract;
import school.hei.asa.repository.jrepository.JContractRepository;
import school.hei.asa.repository.mapper.ContractMapper;
import school.hei.asa.repository.mapper.WorkerMapper;

@Slf4j
@AllArgsConstructor
@Repository
public class ContractRepository {

  private final JContractRepository jContractRepository;
  private final ContractMapper contractMapper;
  private final WorkerMapper workerMapper;

  @Transactional
  public List<Contract> findAllByWorker(Worker worker) {
    return contractMapper.toDomain(
        jContractRepository.findAllByWorkerOrderByEntranceInstantDesc(
            workerMapper.toEntity(worker)));
  }

  @Transactional
  public Optional<Contract> findActiveContractByWorker(Worker worker) {
    return jContractRepository
        .findActiveContractByWorker(workerMapper.toEntity(worker).getCode())
        .map(jContract -> contractMapper.toDomain(List.of(jContract)).getFirst());
  }

  public List<Contract> findAll() {
    return contractMapper.toDomain(jContractRepository.findAll());
  }

  public List<Contract> findByYearBetween(int startYearIncluded, int endYearExcluded) {
    return contractMapper.toDomain(
        jContractRepository.findByYearBetween(startYearIncluded, endYearExcluded));
  }

  public List<Contract> findByYear(int year) {
    return findByYearBetween(year, year + 1);
  }

  public List<Contract> findAllActiveContracts() {
    return contractMapper.toDomain(jContractRepository.findActiveContracts());
  }
}
