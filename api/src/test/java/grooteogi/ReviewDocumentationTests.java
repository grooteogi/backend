package grooteogi;

import static grooteogi.ApiDocumentUtils.getDocumentRequest;
import static grooteogi.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import grooteogi.config.UserInterceptor;
import grooteogi.controller.ReviewController;
import grooteogi.dto.ReviewDto;
import grooteogi.service.ReviewService;
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
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles("test")
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(ReviewController.class)
public class ReviewDocumentationTests {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private ReviewService reviewService;
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

  private ReviewDto.Request request =
      ReviewDto.Request.builder()
          .postId(1)
          .reservationId(1)
          .score(5L)
          .text("만나서 반가웠어요")
          .build();
  private int reviewId = 1;

  @BeforeEach
  void setUp(WebApplicationContext webApplicationContext,
      RestDocumentationContextProvider restDocumentation) {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(
        documentationConfiguration(restDocumentation).operationPreprocessors()
            .withRequestDefaults(prettyPrint()).withResponseDefaults(prettyPrint())).build();

  }

  @Test
  @DisplayName("리뷰 생성")
  void createReview() throws Exception {

    // given
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(session);

    reviewService.createReview(request, session.getId());

    String json = objectMapper.writeValueAsString(request);

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders.post("/review")
            .characterEncoding("utf-8")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
    );

    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("review-create", getDocumentRequest(), getDocumentResponse(),
                requestFields(
                    fieldWithPath("postId").description("포스트 ID"),
                    fieldWithPath("reservationId").description("예약 ID"),
                    fieldWithPath("text").description("리뷰 메세지"),
                    fieldWithPath("score").description("평점")
                ),
                responseFields(
                    fieldWithPath("status").description("결과 코드"),
                    fieldWithPath("message").description("응답 메세지")
                ))
        );
  }

  @Test
  @DisplayName("리뷰 수정")
  void modifyReview() throws Exception {
    // given

    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(session);

    reviewService.modifyReview(request, reviewId, session.getId());

    String json = objectMapper.writeValueAsString(request);

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .patch("/review/{reviewId}", reviewId)
            .content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));

    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("review-modify", getDocumentRequest(), getDocumentResponse(),
                pathParameters(
                    parameterWithName("reviewId").description("리뷰 ID")),
                requestFields(
                    fieldWithPath("postId").description("포스트 ID"),
                    fieldWithPath("reservationId").description("예약 ID"),
                    fieldWithPath("text").description("리뷰 메세지"),
                    fieldWithPath("score").description("평점")
                ),
                responseFields(
                    fieldWithPath("status").description("결과 코드"),
                    fieldWithPath("message").description("응답 메세지")
                ))
        );
  }

  @Test
  @DisplayName("리뷰 삭제")
  void deleteReview() throws Exception {
    // given

    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(session);

    reviewService.deleteReview(reviewId, session.getId());

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .delete("/review/{reviewId}", reviewId));
    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(document("review-delete", getDocumentRequest(), getDocumentResponse(),
            pathParameters(
                parameterWithName("reviewId").description("리뷰 ID")))
        );

  }
}
