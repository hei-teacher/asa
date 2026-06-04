package school.hei.asa.service;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

@Component
public class PDFScrapper {
  public long extractTotalAmount(File file) {
    try (var doc = PDDocument.load(file)) {
      var stripper = new PDFTextStripper();
      var text = stripper.getText(doc);

      var pattern = Pattern.compile("Total[^\\d]*(\\d[\\d\\s]*)");
      var matcher = pattern.matcher(text);

      if (matcher.find()) {
        var raw = matcher.group(1).replaceAll("[\\s\\u00A0]", "").trim();
        try {
          return Long.parseLong(raw);
        } catch (NumberFormatException e) {
          throw new RuntimeException("Invalid amount extracted: " + raw, e);
        }
      }

      throw new RuntimeException("No total amount found in PDF");

    } catch (IOException e) {
      throw new RuntimeException("Error reading PDF", e);
    }
  }
}
