package enterprise.test.infra.s3;

import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class AmazonS3Impl implements AmazonS3 {


    @Override
    public void putObject(String bucket, String fileName, File file) {

    }

    @Override
    public String getUrl(String bucket, String fileName) {
        return fileName;
    }
}
