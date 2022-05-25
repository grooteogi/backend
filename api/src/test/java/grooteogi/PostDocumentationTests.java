package grooteogi;

import static org.mockito.ArgumentMatchers.any;
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
import grooteogi.dto.PostDto;
import grooteogi.dto.PostDto.Response;
import grooteogi.mapper.PostMapper;
import grooteogi.service.PostService;
import grooteogi.utils.JwtProvider;
import grooteogi.utils.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

  @InjectMocks
  private PostMapper postMapper = PostMapper.INSTANCE;

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
    PostDto.Request request = any();
    PostDto.CreateResponse createResponse = any();

    given(postService.createPost(eq(request), session.getId())).willReturn(createResponse);

    String json = objectMapper.writeValueAsString(request);

    // when
    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders.post("/post").characterEncoding("utf-8")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
    );

    resultActions.andExpect(status().isOk())
        .andDo(print());

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


    verify(postService).deletePost(postId, postId);
  }

  @Test
  @DisplayName("포스트 수정")
  public void modifyPost() throws Exception {
    //given
    int postId = anyInt();
    int userId = anyInt();
    PostDto.Request request = any();
    Response response = any();

    given(postService.modifyPost(eq(request), postId, userId)).willReturn(response);

    String json = objectMapper.writeValueAsString(request);

    // when
    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders.put("/post/{postId}", postId)
            .characterEncoding("utf-8")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
    );

    resultActions.andExpect(status().isOk())
        .andDo(print());

  }

  @Test
  @DisplayName("포스트 조회")
  public void getPost() throws Exception {
    // given
    int postId = anyInt();
    Response createResponse = any();

    // when
    given(postService.getPostResponse(postId)).willReturn(createResponse);

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .get("/post/{postId}", postId)
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON));
    resultActions.andExpect(status().isOk())
        .andDo(print());

  }

  @Test
  @DisplayName("포스트 검색")
  public void search() throws Exception {
    // given
    String keyword = anyString();
    Pageable page = PageRequest.of(0, 20);
    String filter = anyString();
    String region = anyString();

    PostDto.SearchResponse response = PostDto.SearchResponse.builder().build();

    // when
    given(postService.search(keyword, filter, eq(page), region)).willReturn(response);

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .get("/post?keyword={keyword}&page={page}&filter={filter}",
                keyword, 1, filter)
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON));

    resultActions.andExpect(status().isOk())
        .andDo(print());

  }


}