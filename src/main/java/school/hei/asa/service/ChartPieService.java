package school.hei.asa.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.Optional;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Service;

@Service
public class ChartPieService {
  private final String tempDirPath = System.getProperty("java.io.tmpdir");
  private static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

  public String generatePieChartImage(DefaultPieDataset dataset) {
    String timestamp = LocalDateTime.now().format(formatter);
    String fileName = timestamp + "-chart.png";

    JFreeChart chart =
        ChartFactory.createPieChart(
            "Distribution of days worked by product", dataset, true, true, false);

    PiePlot plot = (PiePlot) chart.getPlot();
    Color[] colors = {
      new Color(46, 52, 89),
      new Color(206, 111, 143),
      new Color(122, 92, 204),
      new Color(0, 163, 204),
      new Color(242, 206, 0),
      new Color(227, 148, 0)
    };
    int itemCount = dataset.getItemCount();
    for (int i = 0; i < Math.min(itemCount, colors.length); i++) {
      plot.setSectionPaint(dataset.getKey(i), colors[i]);
    }

    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      ChartUtils.writeChartAsPNG(out, chart, 900, 600);

      File chartFile = new File(tempDirPath, fileName);
      ChartUtils.saveChartAsPNG(chartFile, chart, 900, 600);
      cleanupOldChartFiles();

      return Base64.getEncoder().encodeToString(out.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException("Failed to generate chart image", e);
    }
  }

  public File getChartFile() {
    File tempDir = new File(tempDirPath);

    return Optional.ofNullable(tempDir.listFiles((dir, name) -> name.endsWith("-chart.png")))
        .flatMap(files -> Arrays.stream(files).max(Comparator.comparing(File::lastModified)))
        .orElseThrow(() -> new RuntimeException("No chart file found in " + tempDirPath));
  }

  private void cleanupOldChartFiles() {
    File tempDir = new File(tempDirPath);
    File[] chartFiles = tempDir.listFiles((dir, name) -> name.endsWith("-chart.png"));
    if (chartFiles == null || chartFiles.length == 0) {
      return;
    }

    File latestFile =
        Arrays.stream(chartFiles).max(Comparator.comparingLong(File::lastModified)).orElse(null);

    for (File file : chartFiles) {
      if (!file.equals(latestFile)) {
        file.delete();
      }
    }
  }
}
