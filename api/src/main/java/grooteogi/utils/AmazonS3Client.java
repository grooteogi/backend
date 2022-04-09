package grooteogi.utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import grooteogi.config.AwsS3Config;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

public class AmazonS3Client {
  private AmazonS3 s3Client;
  private static AmazonS3Client instance;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  private AmazonS3Client() {
    this.s3Client = new AwsS3Config().amazonS3Client();
    this.instance = null;
  }

  public static AmazonS3Client getInstance() {
    if (instance == null) {
      return new AmazonS3Client();
    } else {
      return instance;
    }
  }

  public String uploadImage(MultipartFile file) {
    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentLength(file.getSize());
    objectMetadata.setContentType(file.getContentType());

    try (InputStream inputStream = file.getInputStream()) {
      this.s3Client.putObject(
          new PutObjectRequest(this.bucket, file.getName(), inputStream, objectMetadata)
          .withCannedAcl(CannedAccessControlList.PublicRead));
    } catch (IOException e) {
      throw new ApiException(ApiExceptionEnum.S3_UPLOAD_FAIL_EXCEPTION);
    }

    return this.s3Client.getUrl(this.bucket, file.getName()).toString();
  }
}
