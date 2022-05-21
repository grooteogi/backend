package grooteogi;

import static grooteogi.ApiDocumentUtils.getDocumentRequest;
import static grooteogi.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
import grooteogi.controller.AuthController;
import grooteogi.domain.User;
import grooteogi.dto.auth.Oauthdto;
import grooteogi.enums.LoginType;
import grooteogi.service.AuthService;
import grooteogi.service.UserService;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(AuthController.class)
public class AuthDocumentationTests {

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private AuthService authService;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp(WebApplicationContext webApplicationContext,
      RestDocumentationContextProvider restDocumentation) {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(
        documentationConfiguration(restDocumentation).operationPreprocessors()
            .withRequestDefaults(prettyPrint()).withResponseDefaults(prettyPrint())).build();
  }

  public Oauthdto getUserDto() {
    Oauthdto userDto = new Oauthdto();
    userDto.setType(LoginType.GENERAL);
    userDto.setEmail("groot@example.com");
    userDto.setPassword("groot1234*");
    return userDto;
  }

  private User getTestUser() {
    User user = new User();
    user.setId(1);
    user.setType(LoginType.GENERAL);
    user.setEmail("groot@example.com");
    user.setPassword(passwordEncoder.encode("groot1234*"));
    user.setNickname("groot-1");
    return user;
  }

  @DisplayName("회원가입")
  @Test
  void register() throws Exception {
    //given
    Oauthdto userDto = getUserDto(); // for  request
    User testUser = getTestUser(); // for response

    // when
    given(authService.register(any())).willReturn(testUser);

    // then
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.post("/auth/register").characterEncoding("utf-8")
            .content(objectMapper.writeValueAsString(userDto))
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
    result.andExpect(status().isOk()).andDo(print()).andDo(
        document("auth-register", getDocumentRequest(), getDocumentResponse(),
            requestFields(fieldWithPath("type").description("로그인 타입"),
                fieldWithPath("email").description("이메일"),
                fieldWithPath("password").description("패스워드")),
            responseFields(fieldWithPath("status").description("결과 코드"),
                fieldWithPath("data.id").description("아이디"),
                fieldWithPath("data.type").description("타입"),
                fieldWithPath("data.nickname").description("닉네임"),
                fieldWithPath("data.password").type(JsonFieldType.STRING).description("패스워드"),
                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                fieldWithPath("data.modified").type(JsonFieldType.STRING).description("수정날짜")
                    .optional(),
                fieldWithPath("data.registered").type(JsonFieldType.STRING).description("가입날짜")
                    .optional(),
                fieldWithPath("data.userHashtags").type(JsonFieldType.ARRAY).description("해시태그"),
                fieldWithPath("data.posts").type(JsonFieldType.ARRAY).description("포스트"))));
  }

  @DisplayName("회원탈퇴")
  @Test
  void withdrawal() throws Exception {
    int userId = anyInt();

    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.delete("/auth/withdrawal?user-id={userId}", userId)
            .accept(MediaType.APPLICATION_JSON));

    result.andExpect(status().isOk()).andDo(print()).andDo(
        document("auth-withdrawal", getDocumentRequest(), getDocumentResponse(),
            requestParameters(parameterWithName("user-id").description("유저 아이디"))));
  }
}
