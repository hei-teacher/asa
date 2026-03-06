package school.hei.asa.conf;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import school.hei.asa.endpoint.event.EventProducer;
import school.hei.asa.file.bucket.BucketComponent;

@AutoConfigureMockMvc
public class FacadeITMockedThirdParties extends FacadeIT {
  @LocalServerPort protected int localPort;
  @MockBean protected BucketComponent bucketConfMock;

  @MockBean protected EventProducer eventProducerMock;
}
