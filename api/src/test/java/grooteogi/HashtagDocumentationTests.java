package grooteogi;

import static grooteogi.ApiDocumentUtils.getDocumentRequest;
import static grooteogi.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import grooteogi.config.UserInterceptor;
import grooteogi.controller.HashtagController;
import grooteogi.dto.HashtagDto;
import grooteogi.service.HashtagService;
import grooteogi.utils.JwtProvider;
import grooteogi.utils.Session;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles("test")
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(HashtagController.class)
public class HashtagDocumentationTests {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private HashtagService hashtagService;
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

  private HashtagDto.Request request = 
      HashtagDto.Request.builder()
          .name("개발자")
          .build();
  private HashtagDto.Response response = 
      HashtagDto.Response.builder()
          .hashtagId(1)
          .name("개발자")
          .build();

  @BeforeEach
  void setUp(WebApplicationContext webApplicationContext,
      RestDocumentationContextProvider restDocumentation) {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(
        documentationConfiguration(restDocumentation).operationPreprocessors()
            .withRequestDefaults(prettyPrint()).withResponseDefaults(prettyPrint())).build();
  }

  @Test
  @DisplayName("해시태그 조회")
  void getHashtag() throws Exception {
    // given
    List<HashtagDto.Response> responses = new ArrayList<>();
    responses.add(response);

    given(hashtagService.getHashtag()).willReturn(responses);

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .get("/hashtag")
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON));
    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(document("hashtag-get", getDocumentRequest(), getDocumentResponse(),
            responseFields(
                fieldWithPath("status").description("결과 코드"),
                fieldWithPath("message").description("응답 메세지"),
                fieldWithPath("data[].hashtagId").description("해시태그 ID"),
                fieldWithPath("data[].name").description("해시태그 이름")
            )
        ));
  }

  @Test
  @DisplayName("해시태그 검색")
  void search() throws Exception {

    String keyword = "개발";

    given(hashtagService.search(keyword)).willReturn(response);

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .get("/hashtag/search?keyword={keyword}", keyword)
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON));
    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(document("hashtag-search", getDocumentRequest(), getDocumentResponse(),
            requestParameters(
                parameterWithName("keyword").description("검색어")
            ),
            responseFields(
                fieldWithPath("status").description("결과 코드"),
                fieldWithPath("message").description("응답 메세지"),
                fieldWithPath("data.hashtagId").description("해시태그 ID"),
                fieldWithPath("data.name").description("해시태그 이름")
            )
        ));
  }

  @Test
  @DisplayName("해시태그 생성")
  void createHashtag() throws Exception {

    given(hashtagService.createHashtag(request)).willReturn(response);

    String json = objectMapper.writeValueAsString(request);

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .post("/hashtag")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));

    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(document("hashtag-create", getDocumentRequest(), getDocumentResponse(),
            requestFields(
                fieldWithPath("name").description("해시태그 이름")
            ),
            responseFields(
                fieldWithPath("status").description("결과 코드"),
                fieldWithPath("message").description("응답 메세지"),
                fieldWithPath("data.hashtagId").description("해시태그 ID"),
                fieldWithPath("data.name").description("해시태그 이름")
            )
        ));
  }
}
