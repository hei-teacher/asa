package school.hei.asa.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import school.hei.asa.conf.FacadeIT;
import school.hei.asa.model.Worker;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Slf4j
public class ContractRepositoryTest extends FacadeIT {
    @Autowired ContractRepository contractRepository;

    @Test
    void fetch_all_contracts_for_worker(){
        var worker = newWorker();
        var result = contractRepository.findAllByWorker(worker);

        assertEquals(2, result.size());
    }

    private Worker newWorker(){
        return new Worker("W-P-2024-01", "Lita Andria", "", "", "", "", "", "");
    }
}
