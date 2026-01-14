package school.hei.asa.endpoint.rest.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.endpoint.rest.controller.mapper.ThMissionExecutionMapper;
import school.hei.asa.endpoint.rest.model.th.ThMissionExecution;
import school.hei.asa.model.Mission;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.Product;
import school.hei.asa.model.Worker;

import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThMissionExecutionMapperTest extends FacadeIT {
    @Autowired ThMissionExecutionMapper thMissionExecutionMapper;

    @Test
    void can_map_mission_execution_by_partners(){
        var missionExecution = new MissionExecution(newMission(),newWorker(),LocalDate.of(2025,3,30),0.5d,"comment", Instant.now());
        var expected = new ThMissionExecution("mission-code", "W-P-2024-01", LocalDate.of(2025,3,30),0.5d,"comment",false,false);

        var actual = thMissionExecutionMapper.toTh(missionExecution, false);

        assertEquals(expected,actual);
    }

    @Test
    void can_map_mission_execution_by_student(){
        var missionExecution = new MissionExecution(newMission(),newWorker(),LocalDate.of(2024,3,30),0.5d,"comment", Instant.now());
        var expected = new ThMissionExecution("mission-code", "W-P-2024-01", LocalDate.of(2024,3,30),0.5d,"comment",false,true);

        var actual = thMissionExecutionMapper.toTh(missionExecution, false);

        assertEquals(expected,actual);
    }

    private Mission newMission(){
        var product = new Product("product-code", "product-name", "product-description");
        return new Mission("mission-code", "mission-title", "mission-description", 0, product);
    }

    private Worker newWorker(){
        return new Worker("W-P-2024-01", "Lita Andria", "", "", "", "", "", "");
    }
}
