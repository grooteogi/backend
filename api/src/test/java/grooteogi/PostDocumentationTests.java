package grooteogi;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import grooteogi.config.UserInterceptor;
import grooteogi.controller.PostController;
import grooteogi.domain.Post;
import grooteogi.domain.User;
import grooteogi.dto.PostDto;
import grooteogi.enums.CreditType;
import grooteogi.mapper.PostMapper;
import grooteogi.service.PostService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
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
@WebMvcTest(PostController.class)
public class PostDocumentationTests {
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private PostService postService;
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
  @DisplayName("포스트 생성")
  public void createPost() throws Exception {
    // given
    PostDto.Request request = postReq();
    PostDto.Response response = postRes();

    given(postService.createPost(eq(request))).willReturn(response);

    String json = objectMapper.writeValueAsString(request);

    // when
    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders.post("/post").characterEncoding("utf-8")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
    );

    // then
    resultActions.andExpect(status().isOk())
        .andDo(print());

  }

  private PostDto.Response postRes() {
    PostDto.Response response = PostMapper.INSTANCE.toResponseDto(ApiDocumentUtils.getPost());
    return response;
  }

  @Test
  @DisplayName("포스트 삭제")
  public void deletePost() throws Exception {
    // given
    int postId = anyInt();

    //when
    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .delete("/post/{postId}", postId)
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON)
    );

    //then
    resultActions.andExpect(status().isOk())
        .andDo(print());

    verify(postService).deletePost(postId);
  }

  @Test
  @DisplayName("포스트 수정")
  public void modifyPost() throws Exception {
    //given
    int postId = anyInt();
    PostDto.Request request = postReq();
    PostDto.Response response = postRes();

    given(postService.modifyPost(eq(request), postId)).willReturn(response);

    String json = objectMapper.writeValueAsString(request);

    // when
    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders.put("/post/{postId}", postId).characterEncoding("utf-8")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
    );

    // then
    resultActions.andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("포스트 조회")
  public void getPost() throws Exception {
    // given
    PostDto.Response response = postRes();
    int postId = anyInt();

    // when
    given(postService.getPost(postId)).willReturn(response);

    // then
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders
            .get("/post/{postId}", postId)
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON));
    result.andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("포스트 검색")
  public void search() throws Exception {
    // given
    String search = anyString();
    String type = anyString();
    Pageable page = PageRequest.of(0, 20);

    PostDto.Response post = postRes();
    List<PostDto.Response> response = new ArrayList<>();
    response.add(post);

    // when
    given(postService.search(search, type, eq(page))).willReturn(response);

    // then
    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders
            .get("/post?search={search}&page={page}&type={type}", search, 1, type)
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON));
    result.andExpect(status().isOk())
        .andDo(print());
  }

  private Post postEntity() {
    Post post =  ApiDocumentUtils.getPost();
    post.setUser(User.builder().build());
    return post;
  }

  private PostDto.Request postReq() {
    return PostDto.Request.builder()
        .userId(1)
        .content("이건 내용")
        .credit(CreditType.DIRECT)
        .title("이건 제목")
        .hashtags(ApiDocumentUtils.getPostHashtagStrings())
        .imageUrl("이미지 주소")
        .schedules(ApiDocumentUtils.getScheduleReqs())
        .build();
  }
}