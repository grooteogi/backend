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
import grooteogi.controller.AuthController;
import grooteogi.dto.AuthDto;
import grooteogi.dto.auth.Token;
import grooteogi.service.AuthService;
import grooteogi.service.UserService;
import grooteogi.utils.EmailClient;
import grooteogi.utils.JwtProvider;
import grooteogi.utils.OauthClient;
import grooteogi.utils.RedisClient;
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
@WebMvcTest(AuthController.class)
public class AuthDocumentationTests {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private UserService userService;
  @MockBean
  private AuthService authService;
  @MockBean
  private OauthClient oauthClient;
  @MockBean
  private EmailClient emailClient;
  @MockBean
  private RedisClient redisClient;
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

  private final AuthDto.Request request = AuthDto.Request.builder()
      .email("groot@example.com")
      .password("groot1234*")
      .build();
  private final AuthDto.Response response = AuthDto.Response.builder()
      .imageUrl("이미지")
      .nickname("groot")
      .build();
  private final AuthDto.SendEmailRequest sendEmailRequest = AuthDto.SendEmailRequest.builder()
      .email("groot@example.com")
      .build();
  private final AuthDto.CheckEmailRequest checkEmailRequest = AuthDto.CheckEmailRequest.builder()
      .code("1234*")
      .email("groot@example.com")
      .build();

  @BeforeEach
  void setUp(WebApplicationContext webApplicationContext,
      RestDocumentationContextProvider restDocumentation) {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(
        documentationConfiguration(restDocumentation).operationPreprocessors()
            .withRequestDefaults(prettyPrint()).withResponseDefaults(prettyPrint())).build();
  }

  @Test
  @DisplayName("로그인")
  public void login() throws Exception {

    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(session);

    grooteogi.domain.User user = grooteogi.domain.User.builder().build();
    given(userService.getUserByEmail(request)).willReturn(
        user
    );

    AuthDto.Response response = AuthDto.Response.builder()
        .nickname("groot")
        .imageUrl("image")
        .build();

    given(authService.login(user, request)).willReturn(new Token("sss", "ssss"));
    given(userService.getAuthResponse(user)).willReturn(response);

    String json = objectMapper.writeValueAsString(request);

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .post("/auth/login")
            .characterEncoding("utf-8")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));

    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("login", getDocumentRequest(), getDocumentResponse(),
                requestFields(
                    fieldWithPath("email").description("이메일"),
                    fieldWithPath("password").description("비밀번호")
                ),
                responseFields(
                    fieldWithPath("status").description("결과 코드"),
                    fieldWithPath("message").description("응답 메세지"),
                    fieldWithPath("data.nickname").description("닉네임"),
                    fieldWithPath("data.imageUrl").description("이미지")
                )));
  }

  @Test
  @DisplayName("회원가입")
  public void register() throws Exception {

    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(session);

    given(authService.register(request)).willReturn(response);

    String json = objectMapper.writeValueAsString(request);

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .post("/auth/register")
            .characterEncoding("utf-8")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));

    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("register", getDocumentRequest(), getDocumentResponse(),
                requestFields(
                    fieldWithPath("email").description("이메일"),
                    fieldWithPath("password").description("비밀번호")
                ),
                responseFields(
                    fieldWithPath("status").description("결과 코드"),
                    fieldWithPath("message").description("응답 메세지"),
                    fieldWithPath("data.nickname").description("닉네임"),
                    fieldWithPath("data.imageUrl").description("이미지")
                )));

  }

  @Test
  @DisplayName("회원 삭제")
  public void withdrawal() throws Exception {

    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(session);

    authService.withdrawal(session.getId());

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .delete("/auth/withdrawal")
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON));

    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("withdrawal", getDocumentRequest(), getDocumentResponse(),
                responseFields(
                    fieldWithPath("status").description("결과 코드"),
                    fieldWithPath("message").description("응답 메세지")
                )));

  }

  @Test
  @DisplayName("이메일 인증코드 전송")
  public void sendVerifyEmail() throws Exception {
    authService.sendVerifyEmail(sendEmailRequest.getEmail());

    String json = objectMapper.writeValueAsString(sendEmailRequest);

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .post("/auth/email/send")
            .characterEncoding("utf-8")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));

    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("send-VerifyEmail", getDocumentRequest(), getDocumentResponse(),
                requestFields(
                    fieldWithPath("email").description("이메일")
                ),
                responseFields(
                    fieldWithPath("status").description("결과 코드"),
                    fieldWithPath("message").description("응답 메세지")
                )));

  }

  @Test
  @DisplayName("이메일 인증 확인")
  public void checkVerifyEmail() throws Exception {

    authService.checkVerifyEmail(checkEmailRequest);

    String json = objectMapper.writeValueAsString(checkEmailRequest);

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .post("/auth/email/check")
            .characterEncoding("utf-8")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));

    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("check-VerifyEmail", getDocumentRequest(), getDocumentResponse(),
                requestFields(
                    fieldWithPath("email").description("이메일"),
                    fieldWithPath("code").description("인증코드")
                ),
                responseFields(
                    fieldWithPath("status").description("결과 코드"),
                    fieldWithPath("message").description("응답 메세지")
                )));

  }
}
