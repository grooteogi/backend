package grooteogi;

import static grooteogi.ApiDocumentUtils.getDocumentRequest;
import static grooteogi.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import grooteogi.config.UserInterceptor;
import grooteogi.controller.UserController;
import grooteogi.dto.ProfileDto;
import grooteogi.dto.UserDto;
import grooteogi.service.UserService;
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
@WebMvcTest(UserController.class)
public class UserDocumentationTests {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private UserService userService;
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

  //DTO
  private final ProfileDto.Request request =
      ProfileDto.Request.builder()
          .address("인천 계양구")
          .imageUrl("이미지")
          .name("김영희")
          .nickname("groot")
          .phone("01012345678")
          .build();
  private final ProfileDto.Response response =
      ProfileDto.Response.builder()
      .address("인천 계양구")
      .email("groot@example.com")
      .nickname("groot")
      .name("김영희")
      .imageUrl("이미지")
      .phone("01012345678")
      .build();
  private final UserDto.PasswordRequest passwordRequest =
      UserDto.PasswordRequest.builder()
      .password("groot1234*")
      .build();

  @BeforeEach
  void setUp(WebApplicationContext webApplicationContext,
      RestDocumentationContextProvider restDocumentation) {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(
        documentationConfiguration(restDocumentation).operationPreprocessors()
            .withRequestDefaults(prettyPrint()).withResponseDefaults(prettyPrint())).build();
  }

  @Test
  @DisplayName("사용자 프로필 조회")
  public void getUserProfile() throws Exception {
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(session);

    given(userService.getUserProfile(session.getId())).willReturn(response);

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .get("/user/profile")
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON));
    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("profile-get", getDocumentRequest(), getDocumentResponse(),
                responseFields(
                    fieldWithPath("status").description("결과 코드"),
                    fieldWithPath("message").description("응답 메세지"),
                    fieldWithPath("data.nickname").description("닉네임"),
                    fieldWithPath("data.email").description("이메일"),
                    fieldWithPath("data.name").description("이름"),
                    fieldWithPath("data.phone").description("핸드폰 번호"),
                    fieldWithPath("data.address").description("주소"),
                    fieldWithPath("data.imageUrl").description("이미지 url")
                )));
  }

  @Test
  @DisplayName("사용자 프로필 수정")
  public void modifyUserProfile() throws Exception {
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(session);

    userService.modifyUserProfile(session.getId(), request);
    String json = objectMapper.writeValueAsString(request);

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .patch("/user/profile")
            .characterEncoding("utf-8")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));
    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("profile-modify", getDocumentRequest(), getDocumentResponse(),
                requestFields(
                    fieldWithPath("nickname").description("닉네임"),
                    fieldWithPath("name").description("이름"),
                    fieldWithPath("address").description("주소"),
                    fieldWithPath("imageUrl").description("이미지"),
                    fieldWithPath("phone").description("핸드폰 번호")
                ),
                responseFields(
                    fieldWithPath("status").description("결과 코드"),
                    fieldWithPath("message").description("응답 메세지")
                )));
  }

  @Test
  @DisplayName("사용자 비밀번호 변경")
  public void modifyUserPw() throws Exception {
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(session);

    userService.modifyUserPw(session.getId(), passwordRequest);
    String json = objectMapper.writeValueAsString(passwordRequest);
    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .patch("/user/password")
            .characterEncoding("utf-8")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));
    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("password-modify", getDocumentRequest(), getDocumentResponse(),
                requestFields(
                    fieldWithPath("password").description("비밀번호")
                ),
                responseFields(
                    fieldWithPath("status").description("결과 코드"),
                    fieldWithPath("message").description("응답 메세지")
                )));
  }

}
