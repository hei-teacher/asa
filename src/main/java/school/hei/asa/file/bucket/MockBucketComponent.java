package school.hei.asa.file.bucket;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.time.Duration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import school.hei.asa.file.hash.FileHash;
import school.hei.asa.file.hash.FileHashAlgorithm;

@Profile("mock")
@Component
public class MockBucketComponent implements BucketComponent {

    @Override
    public FileHash upload(File file, String bucketKey) {
        return new FileHash(FileHashAlgorithm.NONE, null);
    }

    @Override
    public File download(String bucketKey) {
        try {
            File temp = File.createTempFile("mock-", ".txt");
            Files.writeString(temp.toPath(), "mock content for " + bucketKey);
            temp.deleteOnExit();
            return temp;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public URL presign(String bucketKey, Duration expiration) {
        try {
            return new URL("http://localhost/mock/" + bucketKey);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBucketName() {
        return "mock-bucket";
    }
}