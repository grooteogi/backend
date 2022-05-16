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
import grooteogi.dto.PostDto;
import grooteogi.dto.ScheduleDto;
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

    resultActions.andExpect(status().isOk())
        .andDo(print());

  }

  private PostDto.Response postRes() {
    return PostDto.Response.builder().postId(1).build();
  }

  private PostDto.Request postReq() {
    return PostDto.Request
        .builder()
        .userId(1)
        .title("제목입니다")
        .content("내용입니다")
        .credit(CreditType.DIRECT)
        .imageUrl("이미지입니다")
        .hashtags(postHashtags())
        .schedules(scheduleReqs())
        .build();
  }

  private List<ScheduleDto.Request> scheduleReqs() {
    List<ScheduleDto.Request> schedules = new ArrayList<>();
    ScheduleDto.Request schedule = ScheduleDto.Request
        .builder()
        .date("2022-05-01")
        .startTime("12:00:00")
        .endTime("13:00:00")
        .region("어디게")
        .place("여기다")
        .build();
    schedules.add(schedule);
    return schedules;
  }

  private String[] postHashtags() {
    return new String[]{"해시태그1", "해시태그2"};
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

    resultActions.andExpect(status().isOk())
        .andDo(print());

  }

  @Test
  @DisplayName("포스트 조회")
  public void getPost() throws Exception {
    // given
    int postId = anyInt();
    PostDto.Response response = postRes();

    // when
    given(postService.getPost(postId)).willReturn(response);

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
    String sort = anyString();

    PostDto.Response post = postRes();
    List<PostDto.Response> response = new ArrayList<>();
    response.add(post);

    // when
    given(postService.search(keyword, sort, eq(page))).willReturn(response);

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .get("/post?keyword={keyword}&page={page}&sort={sort}", keyword, 1, sort)
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON));

    resultActions.andExpect(status().isOk())
        .andDo(print());

  }

}