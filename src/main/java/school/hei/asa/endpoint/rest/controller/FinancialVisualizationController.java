package school.hei.asa.endpoint.rest.controller;

import static org.springframework.http.MediaType.IMAGE_PNG;

import java.io.FileInputStream;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import school.hei.asa.service.ContractService;

@AllArgsConstructor
@Controller
public class FinancialVisualizationController {

  private final ContractService contractService;

  @GetMapping(value = "/financial-visualization")
  public ResponseEntity<byte[]> financialVisualization(
      @RequestParam int year, @RequestParam int month) throws IOException {
    var file = contractService.contractsFinancialExecutionAsImage(year, month);
    return ResponseEntity.ok()
        .contentType(IMAGE_PNG)
        .body(IOUtils.toByteArray(new FileInputStream(file)));
  }
}
