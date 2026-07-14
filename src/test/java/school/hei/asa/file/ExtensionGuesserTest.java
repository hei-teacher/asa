package school.hei.asa.file;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ExtensionGuesserTest {

  private final ExtensionGuesser extensionGuesser = new ExtensionGuesser();

  @Test
  void detects_txt_extension() {
    var txtBytes = "Hello, World!".getBytes();
    var result = extensionGuesser.apply(txtBytes);
    assertTrue(result.equals(".txt") || result.equals(".asc"));
  }
}
