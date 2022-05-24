package grooteogi;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.amazonaws.services.s3.AmazonS3Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import grooteogi.config.UserInterceptor;
import grooteogi.controller.AwsS3Controller;
import grooteogi.service.AwsS3Service;
import grooteogi.utils.JwtProvider;
import grooteogi.utils.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(AwsS3Controller.class)
class AwsS3DocumentationTests {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private AwsS3Service awsS3Service;
  @MockBean
  private AmazonS3Client amazonS3Client;
  @MockBean
  private UserInterceptor userInterceptor;
  @MockBean
  private JwtProvider jwtProvider;
  @MockBean
  private PasswordEncoder passwordEncoder;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private Session session;
  @MockBean
  private Authentication authentication;
  @MockBean
  private SecurityContext securityContext;

  @BeforeEach
  void setUp(WebApplicationContext webApplicationContext,
      RestDocumentationContextProvider restDocumentation) {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(
        documentationConfiguration(restDocumentation).operationPreprocessors()
            .withRequestDefaults(prettyPrint()).withResponseDefaults(prettyPrint())).build();
  }
  @Test
  @DisplayName("이미지 업로드")
  void uploadImage() throws Exception {
    // given
    MockMultipartFile multipartFile = new MockMultipartFile(
        "image",
        "image.jpeg",
        MediaType.IMAGE_JPEG_VALUE,
        "image".getBytes()
    );

    String response = anyString();

    given(awsS3Service.uploadImage(multipartFile)).willReturn(response);

    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.multipart("/s3/image")
            .file("multipartFile",  multipartFile.getBytes())
            .contentType(MediaType.MULTIPART_FORM_DATA));
    result.andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("이미지 삭제")
  void deleteImage() throws Exception {

    // given
    String fileName = anyString();

    awsS3Service.deleteImage(fileName);

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .delete("/s3/image?fileName={fileName}", fileName)
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON)
    );
    resultActions.andExpect(status().isOk())
        .andDo(print());
  }
}