package grooteogi.controller;

import grooteogi.response.BasicResponse;
import grooteogi.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class AwsS3Controller {

  private final AwsS3Service awsS3Service;

  @PostMapping("/image")
  public ResponseEntity<BasicResponse> uploadImage(@RequestParam MultipartFile multipartFile) {
    String imageUrl = awsS3Service.uploadImage(multipartFile);
    return ResponseEntity.ok(BasicResponse.builder().data(imageUrl).build());
  }

  @DeleteMapping("/image")
  public ResponseEntity<BasicResponse> deleteImage(@RequestParam String fileName) {
    awsS3Service.deleteImage(fileName);
    return ResponseEntity.ok(BasicResponse.builder().build());
  }
}
