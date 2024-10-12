package enterprise.test.common.s3;

import java.io.File;

public interface AmazonS3 {

    void putObject(String bucket, String fileName, File file);

    String getUrl(String bucket, String fileName);
}
