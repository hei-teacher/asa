package school.hei.asa.file.bucket;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import school.hei.asa.file.hash.FileHash;

public interface BucketPort {
    FileHash upload(File file, String bucketKey);

    File download(String bucketKey);

    URL presign(String bucketKey, Duration expiration);

    String getBucketName();
}