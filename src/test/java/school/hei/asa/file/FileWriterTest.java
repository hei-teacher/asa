package school.hei.asa.file;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;

class FileWriterTest {

  private final FileWriter fileWriter = new FileWriter(new ExtensionGuesser());

  @Test
  void writes_bytes_to_temp_file() throws IOException {
    var content = "Hello, World!".getBytes();
    var result = fileWriter.apply(content, null);

    assertNotNull(result);
    assertTrue(result.exists());
    assertTrue(result.getName().endsWith(".txt"));
    assertEquals("Hello, World!", Files.readString(result.toPath()));
    result.delete();
  }

  @Test
  void writes_to_specified_directory() throws IOException {
    var tempDir = Files.createTempDirectory("test-writer").toFile();
    tempDir.deleteOnExit();
    var content = "Test content".getBytes();

    var result = fileWriter.apply(content, tempDir);

    assertTrue(result.getParentFile().equals(tempDir));
    assertTrue(result.exists());
    assertEquals("Test content", Files.readString(result.toPath()));
    result.delete();
    tempDir.delete();
  }
}
