package grooteogi.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class AwsS3Service {
  private final AmazonS3Client amazonS3Client;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  public String uploadImage(MultipartFile file) {
    String fileName = createFileName(file.getOriginalFilename());
    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentLength(file.getSize());
    objectMetadata.setContentType(file.getContentType());

    try (InputStream inputStream = file.getInputStream()) {
      this.amazonS3Client.putObject(
          new PutObjectRequest(this.bucket, fileName, inputStream, objectMetadata)
              .withCannedAcl(CannedAccessControlList.PublicRead));
    } catch (IOException e) {
      throw new ApiException(ApiExceptionEnum.S3_UPLOAD_FAIL_EXCEPTION);
    }

    return this.amazonS3Client.getUrl(this.bucket, fileName).toString();
  }

  public String deleteImage(String fileName) {

    if (!this.amazonS3Client.doesObjectExist(this.bucket, fileName)) {
      throw new ApiException(ApiExceptionEnum.NOT_FOUND_EXCEPTION);
    }
    amazonS3Client.deleteObject(new DeleteObjectRequest(this.bucket, fileName));

    return "삭제가 정상적으로 완료되었습니다.";
  }

  private String createFileName(String fileName) {
    //파일 업로드 시 파일명 난수화
    return UUID.randomUUID().toString().concat(getFileExtension(fileName));
  }

  private String getFileExtension(String fileName) {
    // file 형식이 잘못된 경우(.의 존재 부재)를 확인
    try {
      return fileName.substring(fileName.lastIndexOf("."));
    } catch (StringIndexOutOfBoundsException e) {
      throw new ApiException(ApiExceptionEnum.BAD_FILE_FORM_EXCEPTION);
    }
  }
}
