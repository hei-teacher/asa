package school.hei.asa.unit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.hei.asa.service.PDFScrapper;

@ExtendWith(MockitoExtension.class)
class PDFScrapperTest {

  @InjectMocks private PDFScrapper pdfScrapper;

  File mockFile;

  @BeforeEach
  void setUp() {
    mockFile = mock(File.class);
  }

  private PDDocument createDocumentWithText(String text) throws IOException {
    PDDocument doc = new PDDocument();
    PDPage page = new PDPage();
    doc.addPage(page);

    try (PDPageContentStream stream = new PDPageContentStream(doc, page)) {
      stream.beginText();
      stream.setFont(PDType1Font.HELVETICA, 12);
      stream.newLineAtOffset(100, 700);
      stream.showText(text);
      stream.endText();
    }
    return doc;
  }

  private File createTempPdf(String text) throws IOException {
    PDDocument doc = createDocumentWithText(text);
    File tmp = File.createTempFile("test-pdf", ".pdf");
    doc.save(tmp);
    doc.close();
    tmp.deleteOnExit();
    return tmp;
  }

  @Test
  void shouldExtractSimpleTotal() throws IOException {
    File pdf = createTempPdf("Total 123456");
    assertThat(pdfScrapper.extractTotalAmount(pdf)).isEqualTo(123456L);
  }

  @Test
  void shouldExtractTotalWithSpaces() throws IOException {
    File pdf = createTempPdf("Total 1 234 567");
    assertThat(pdfScrapper.extractTotalAmount(pdf)).isEqualTo(1234567L);
  }

  @Test
  void shouldExtractTotalWithColon() throws IOException {
    File pdf = createTempPdf("Total: 9900");
    assertThat(pdfScrapper.extractTotalAmount(pdf)).isEqualTo(9900L);
  }

  @Test
  void shouldExtractTotalAmountLabel() throws IOException {
    File pdf = createTempPdf("Total Amount 42000");
    assertThat(pdfScrapper.extractTotalAmount(pdf)).isEqualTo(42000L);
  }

  @Test
  void shouldThrowWhenNoTotalFound() throws IOException {
    File pdf = createTempPdf("Invoice #001 - No total here");

    assertThatThrownBy(() -> pdfScrapper.extractTotalAmount(pdf))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("No total amount found in PDF");
  }

  @Test
  void shouldThrowWhenFileIsInvalid() {
    File badFile = new File("nonexistent.pdf");

    assertThatThrownBy(() -> pdfScrapper.extractTotalAmount(badFile))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Error reading PDF");
  }
}
