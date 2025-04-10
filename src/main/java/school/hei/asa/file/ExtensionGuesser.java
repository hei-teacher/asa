package school.hei.asa.file;

import lombok.SneakyThrows;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypes;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class ExtensionGuesser implements Function<byte[], String> {
  @SneakyThrows
  @Override
  public String apply(byte[] bytes) {
    var tika = new Tika();
    String detectedMediaType = tika.detect(bytes);
    String extension = MimeTypes.getDefaultMimeTypes().forName(detectedMediaType).getExtension();
    return extension.startsWith(".") ? extension : "." + extension;
  }
}
