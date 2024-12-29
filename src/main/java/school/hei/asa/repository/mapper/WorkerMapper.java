package school.hei.asa.repository.mapper;

import org.springframework.stereotype.Component;
import school.hei.asa.model.FullTimeEmployee;
import school.hei.asa.model.PartnerContractor;
import school.hei.asa.model.StudentContractor;
import school.hei.asa.model.Worker;
import school.hei.asa.repository.model.JWorker;

@Component
public class WorkerMapper {
  public Worker toDomain(JWorker jWorker) {
    return switch (jWorker.getWorkerType()) {
      case partnerContractor -> new PartnerContractor(jWorker.getCode(), jWorker.getName());
      case studentContractor -> new StudentContractor(jWorker.getCode(), jWorker.getName());
      case fullTimeEmployee -> new FullTimeEmployee(jWorker.getCode(), jWorker.getName());
    };
  }
}
