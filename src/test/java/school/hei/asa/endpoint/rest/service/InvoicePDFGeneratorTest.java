package school.hei.asa.endpoint.rest.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import school.hei.asa.endpoint.rest.model.th.ThInvoiceForm;
import school.hei.asa.file.FileWriter;
import school.hei.asa.model.Worker;
import school.hei.asa.service.TemplateResolverEngine;

class InvoicePDFGeneratorTest {

  private final FileWriter fileWriter = mock(FileWriter.class);
  private final TemplateResolverEngine templateResolverEngine = mock(TemplateResolverEngine.class);
  private final TemplateEngine templateEngine = mock(TemplateEngine.class);
  private final InvoicePDFGenerator subject =
      new InvoicePDFGenerator(fileWriter, templateResolverEngine);

  private final Worker worker =
      new Worker("W-001", "John", "john@test.com", "John", "Addr", "City", "NIF", "STAT");
  private final ThInvoiceForm thInvoiceForm =
      new ThInvoiceForm(
          "id",
          "2025-01",
          "01/01/2025",
          "03/01/2025",
          "Dev",
          "20.0",
          "50000",
          "1000000",
          false,
          null,
          null,
          null,
          null,
          null,
          null,
          null);

  @BeforeEach
  void setUp() {
    reset(fileWriter, templateResolverEngine, templateEngine);
    when(templateResolverEngine.getTemplateEngine()).thenReturn(templateEngine);
  }

  @Test
  void apply_generates_pdf_and_writes_file() {
    var expectedFile = mock(File.class);
    var htmlContent =
        "<html xmlns=\"http://www.w3.org/1999/xhtml\"><body><p>test</p></body></html>";
    when(templateEngine.process(eq("invoice"), any(Context.class))).thenReturn(htmlContent);
    when(fileWriter.apply(any(), any())).thenReturn(expectedFile);

    var result = subject.apply(worker, thInvoiceForm, "invoice");

    assertSame(expectedFile, result);
    verify(templateEngine).process(eq("invoice"), any(Context.class));
    verify(fileWriter).apply(any(), eq(null));
  }

  @Test
  void apply_uses_correct_template_name() {
    when(templateEngine.process(anyString(), any(Context.class)))
        .thenReturn("<html xmlns=\"http://www.w3.org/1999/xhtml\"><body><p>test</p></body></html>");
    when(fileWriter.apply(any(), any())).thenReturn(mock(File.class));

    subject.apply(worker, thInvoiceForm, "custom-template");

    verify(templateEngine).process(eq("custom-template"), any(Context.class));
  }
}
