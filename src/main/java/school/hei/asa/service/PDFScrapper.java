package school.hei.asa.service;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

@Service
public class PDFScrapper {
  public long extractTotalAmount(File file) {
    try (PDDocument fis = PDDocument.load(file)) {
      PDFTextStripper stripper = new PDFTextStripper();
      String text = stripper.getText(fis);
      Pattern pattern = Pattern.compile("Total\\s+([\\d\\s]+)");
      Matcher matcher = pattern.matcher(text);

      if (matcher.find()) {
        String totalAmount = matcher.group(1).replaceAll("\\s", "").trim();
        return Long.parseLong(totalAmount);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return 0;
  }
}
