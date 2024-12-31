package school.hei.asa.repository.model;

import school.hei.asa.model.FullTimeEmployee;
import school.hei.asa.model.PartnerContractor;
import school.hei.asa.model.StudentContractor;
import school.hei.asa.model.Worker;

public enum WorkerType {
  partnerContractor,
  studentContractor,
  fullTimeEmployee;

  public static WorkerType type(Worker worker) {
    if (worker instanceof PartnerContractor) {
      return partnerContractor;
    }
    if (worker instanceof StudentContractor) {
      return studentContractor;
    }
    if (worker instanceof FullTimeEmployee) {
      return fullTimeEmployee;
    }
    throw new IllegalArgumentException("Unsupported worker type");
  }
}
