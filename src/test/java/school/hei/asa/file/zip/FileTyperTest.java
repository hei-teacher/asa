package school.hei.asa.file.zip;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class FileTyperTest {

  private final FileTyper fileTyper = new FileTyper();

  @Test
  void detects_txt_file() throws IOException {
    var tempFile = Files.createTempFile("test", ".txt").toFile();
    Files.writeString(tempFile.toPath(), "Hello, World!");
    tempFile.deleteOnExit();

    var mediaType = fileTyper.apply(tempFile);

    assertEquals(MediaType.TEXT_PLAIN, mediaType);
  }
}
