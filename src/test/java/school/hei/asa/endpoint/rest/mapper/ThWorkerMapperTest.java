package school.hei.asa.endpoint.rest.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import school.hei.asa.CareProductCodeSupplier;
import school.hei.asa.endpoint.rest.controller.mapper.ThWorkerMapper;
import school.hei.asa.endpoint.rest.model.th.ThWorkerLevelHistory;
import school.hei.asa.model.Mission;
import school.hei.asa.model.MissionExecution;
import school.hei.asa.model.PartnerContractor;
import school.hei.asa.model.Product;
import school.hei.asa.model.Worker;
import school.hei.asa.model.WorkerLevelHistory;
import school.hei.asa.repository.MissionExecutionRepository;
import school.hei.asa.repository.model.JWorkerLevel;

class ThWorkerMapperTest {

    @Test
    void can_map_worker_level_histories_to_th() {
        // Dépendances mockées
        MissionExecutionRepository repo = Mockito.mock(MissionExecutionRepository.class);
        CareProductCodeSupplier supplier = Mockito.mock(CareProductCodeSupplier.class);

        ThWorkerMapper mapper = new ThWorkerMapper(repo, supplier);

        // Worker
        Worker worker = new PartnerContractor(
                "worker-code", "name", "email", "full name",
                "address", "city", "nif", "stat"
        );

        // Niveau
        JWorkerLevel jLevel = new JWorkerLevel();
        jLevel.setLevel("1");
        jLevel.setLevelId("lvl1");

        // Historique de niveau
        WorkerLevelHistory history = new WorkerLevelHistory(
                worker,
                jLevel,
                Instant.parse("2025-01-01T00:00:00Z"),
                "fullTimeEmployee",          // doit devenir "Salarié"
                10,                          // projectedDaysToWork attendu: "10"
                BigDecimal.valueOf(1000),
                "Engineer",
                12
        );

        // Mission & exécution (0.5 jour)
        Product product = new Product("pcode", "pname", "pdesc");
        Mission mission = new Mission("mission-code", "title", "desc", 5, product);
        MissionExecution me = new MissionExecution(
                mission, worker, LocalDate.of(2025, 1, 15), 0.5, "comment",
                Instant.parse("2025-01-16T00:00:00Z")
        );

        // Comportement mock
        Mockito.when(repo.missionExecutionsByDateBetween(Mockito.eq(worker), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(me));
        Mockito.when(supplier.get()).thenReturn("CARE-PRODUCT"); // différent de "pcode" => pas care

        // Appel
        List<ThWorkerLevelHistory> result = mapper.toTh(List.of(history));

        // Assertions
        assertNotNull(result);
        assertEquals(1, result.size());

        ThWorkerLevelHistory th = result.get(0);
        assertEquals("1", th.level());
        assertEquals("Salarié", th.contractType());       // mapping via switch
        assertEquals("10", th.projectedDaysToWork());     // car non null
        assertEquals("0.5", th.actualWorkedDay());        // somme des exécutions
        assertEquals(BigDecimal.valueOf(1000), th.salary());
        assertEquals("Engineer", th.jobTitle());
        assertEquals(12, th.contractDuration());
    }
}
