package school.hei.asa.service;

import org.springframework.beans.factory.annotation.Autowired;
import school.hei.asa.conf.FacadeIT;

@Transactional
class MissionServiceIT extends FacadeIT {

  @Autowired MissionService missionService;

}
