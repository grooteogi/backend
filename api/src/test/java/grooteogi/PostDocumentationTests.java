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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import grooteogi.config.UserInterceptor;
import grooteogi.controller.PostController;
import grooteogi.dto.HashtagDto;
import grooteogi.dto.LikeDto;
import grooteogi.dto.PostDto;
import grooteogi.dto.PostDto.SearchResult;
import grooteogi.dto.ReviewDto;
import grooteogi.dto.ScheduleDto;
import grooteogi.dto.UserDto;
import grooteogi.enums.CreditType;
import grooteogi.service.PostService;
import grooteogi.utils.JwtProvider;
import grooteogi.utils.Session;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
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
  private HttpServletRequest httpServletRequest;
  @MockBean
  private Authentication authentication;
  @MockBean
  private SecurityContext securityContext;

  private final int postId = 1;
  private final String[] hashtags = new String[]{"?????????", "??????"};
  private final ScheduleDto.Request schedulesRequest =
      ScheduleDto.Request.builder()
          .date("2022-05-25")
          .startTime("12::00:00")
          .endTime("13:00:00")
          .place("?????????")
          .region("?????????")
          .build();
  private final List<ScheduleDto.Request> schedules = new ArrayList<>();
  private final PostDto.CreateResponse createResponse =
      PostDto.CreateResponse.builder()
          .postId(1)
          .build();
  private final LikeDto.Response likes =
      LikeDto.Response.builder()
          .count(1)
          .liked(true)
          .build();
  private final UserDto.Response mentor =
      UserDto.Response.builder()
          .imageUrl("?????? ????????? ??????")
          .nickname("???????????? ?????????")
          .userId(1)
          .build();
  private final PostDto.Response response =
      PostDto.Response.builder()
          .createAt("2022-05-26")
          .imageUrl("????????? ????????? ??????")
          .content("???????????? ???????????? ???????????????.")
          .title("????????? ??????????????????.")
          .creditType(CreditType.DIRECT)
          .hashtags(hashtags)
          .likes(likes)
          .mentor(mentor)
          .build();

  private final PostDto.SearchResult post =
      PostDto.SearchResult.builder()
          .postId(1)
          .hashtags(hashtags)
          .imageUrl("????????? ????????? ??????")
          .content("???????????? ???????????? ???????????????.")
          .title("????????? ??????????????????.")
          .build();

  @BeforeEach
  void setUp(WebApplicationContext webApplicationContext,
      RestDocumentationContextProvider restDocumentation) {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(
        documentationConfiguration(restDocumentation).operationPreprocessors()
            .withRequestDefaults(prettyPrint()).withResponseDefaults(prettyPrint())).build();

  }

  @Test
  @DisplayName("????????? ??????")
  public void search() throws Exception {
    // given
    final String keyword = "???";
    final int page = 1;
    Pageable pages = PageRequest.of(page - 1, 12, Sort.by("id").descending());
    final String filter = "LATEST";
    final String region = "?????????";
    final String hashtag = "????????????";
    final List<SearchResult> posts = new ArrayList<>();

    posts.add(post);
    PostDto.SearchResponse response =
        PostDto.SearchResponse.builder()
            .posts(posts)
            .pageCount(1)
            .build();

    // when
    when(postService.search(keyword, filter, pages, hashtag, region)).thenReturn(response);

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .get("/post/search?keyword=???&page=1&filter=LATEST&hashtag=????????????&region=?????????")
    );
    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("post-search", getDocumentRequest(), getDocumentResponse(),
                requestParameters(
                    parameterWithName("keyword").description("?????????"),
                    parameterWithName("page").description("????????? ??????"),
                    parameterWithName("filter").description("?????? ??????"),
                    parameterWithName("hashtag").description("????????????"),
                    parameterWithName("region").description("?????? ??????")
                ),
                responseFields(
                    fieldWithPath("status").description("?????? ??????"),
                    fieldWithPath("message").description("?????? ?????????"),
                    fieldWithPath("data.posts[].postId").description("????????? ID"),
                    fieldWithPath("data.posts[].title").description("????????? ??????"),
                    fieldWithPath("data.posts[].content").description("????????? ??????"),
                    fieldWithPath("data.posts[].imageUrl").description("????????? ????????? url"),
                    fieldWithPath("data.posts[].hashtags[]").description("???????????? ??????"),
                    fieldWithPath("data['pageCount']").description("??? ????????? ???")
                ))
        );

  }

  @Test
  @DisplayName("????????? ??????")
  public void getPost() throws Exception {

    // when
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(session);

    given(postService.getPostResponse(postId,
        httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION))).willReturn(response);

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .get("/post/detail/{postId}", postId)
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON));
    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("post-get", getDocumentRequest(), getDocumentResponse(),
                pathParameters(
                    parameterWithName("postId").description("????????? ID")
                ),
                responseFields(
                    fieldWithPath("status").description("?????? ??????"),
                    fieldWithPath("message").description("?????? ?????????"),
                    fieldWithPath("data.postId").description("????????? ID"),
                    fieldWithPath("data.title").description("????????? ??????"),
                    fieldWithPath("data.content").description("????????? ??????"),
                    fieldWithPath("data.imageUrl").description("????????? ????????? url"),
                    fieldWithPath("data.createAt").description("????????? ?????? ??????"),
                    fieldWithPath("data.hashtags[]").description("???????????? ??????"),
                    fieldWithPath("data.creditType").description("?????? ??????"),
                    fieldWithPath("data.likes.count").description("??? ???"),
                    fieldWithPath("data.likes.liked").description("??? ??????"),
                    fieldWithPath("data.mentor.userId").description("?????? ID"),
                    fieldWithPath("data.mentor.nickname").description("?????? ?????????"),
                    fieldWithPath("data.mentor.imageUrl").description("?????? ????????? url")
                ))
        );

  }

  @Test
  @DisplayName("????????? ??????")
  void getSchedules() throws Exception {

    List<ScheduleDto.Response> responses = new ArrayList<>();
    ScheduleDto.Response response =
        ScheduleDto.Response.builder()
            .date("2022-05-25")
            .startTime("12::00:00")
            .endTime("13:00:00")
            .place("?????????")
            .region("?????????")
            .scheduleId(1)
            .build();
    responses.add(response);

    given(postService.getSchedulesResponse(postId)).willReturn(responses);

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .get("/post/schedules/{postId}", postId)
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON));
    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("schedule-get", getDocumentRequest(), getDocumentResponse(),
                pathParameters(
                    parameterWithName("postId").description("????????? ID")
                ),
                responseFields(
                    fieldWithPath("status").description("?????? ??????"),
                    fieldWithPath("message").description("?????? ?????????"),
                    fieldWithPath("data.[].scheduleId").description("????????? ID"),
                    fieldWithPath("data.[].date").description("?????? ??????"),
                    fieldWithPath("data.[].startTime").description("?????? ?????? ??????"),
                    fieldWithPath("data.[].endTime").description("?????? ?????? ??????"),
                    fieldWithPath("data.[].region").description("?????? ??????"),
                    fieldWithPath("data.[].place").description("?????? ??????")
                ))
        );

  }

  @Test
  @DisplayName("?????? ??????")
  void getReview() throws Exception {

    List<ReviewDto.Response> responses = new ArrayList<>();
    ReviewDto.Response response =
        ReviewDto.Response.builder()
            .reviewId(1)
            .createAt("2022-05-25")
            .imageUrl("????????? ????????? url")
            .nickname("???????????? ?????????")
            .score(5)
            .text("????????? ?????????????????????.")
            .build();
    responses.add(response);

    given(postService.getReviewsResponse(postId)).willReturn(responses);

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .get("/post/reviews/{postId}", postId)
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON));
    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("review-get", getDocumentRequest(), getDocumentResponse(),
                pathParameters(
                    parameterWithName("postId").description("????????? ID")
                ),
                responseFields(
                    fieldWithPath("status").description("?????? ??????"),
                    fieldWithPath("message").description("?????? ?????????"),
                    fieldWithPath("data.[].reviewId").description("?????? ID"),
                    fieldWithPath("data.[].score").description("??????"),
                    fieldWithPath("data.[].nickname").description("?????? ?????????"),
                    fieldWithPath("data.[].imageUrl").description("?????? ????????? url "),
                    fieldWithPath("data.[].createAt").description("?????? ?????? ??????"),
                    fieldWithPath("data.[].text").description("?????? ???")
                ))
        );

  }

  @Test
  @DisplayName("??? ??????")
  void createHeart() throws Exception {

    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(session);

    postService.modifyHeart(postId, session.getId());

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .get("/post/{postId}/like", postId)
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON));
    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("like-get", getDocumentRequest(), getDocumentResponse(),
                pathParameters(
                    parameterWithName("postId").description("????????? ID")
                ),
                responseFields(
                    fieldWithPath("status").description("?????? ??????"),
                    fieldWithPath("message").description("?????? ?????????")
                ))
        );

  }

  @Test
  @DisplayName("???????????? ??????")
  void getHashtags() throws Exception {

    List<HashtagDto.Response> responses = new ArrayList<>();
    HashtagDto.Response response =
        HashtagDto.Response.builder()
            .name("?????????")
            .build();
    responses.add(response);

    given(postService.getHashtagsResponse(postId)).willReturn(responses);

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .get("/post/{postId}/hashtags", postId)
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON));
    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("hashtags-get", getDocumentRequest(), getDocumentResponse(),
                pathParameters(
                    parameterWithName("postId").description("????????? ID")
                ),
                responseFields(
                    fieldWithPath("status").description("?????? ??????"),
                    fieldWithPath("message").description("?????? ?????????"),
                    fieldWithPath("data.[].name").description("???????????? ??????")
                ))
        );

  }

  @Test
  @DisplayName("????????? ??????")
  public void createPost() throws Exception {
    // given
    schedules.add(schedulesRequest);
    final PostDto.Request request =
        PostDto.Request.builder()
            .imageUrl("????????? ??????")
            .content("???????????? ???????????? ???????????????.")
            .title("????????? ??????????????????.")
            .creditType(CreditType.DIRECT)
            .schedules(schedules)
            .hashtags(hashtags)
            .build();

    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(session);

    given(postService.createPost(request, session.getId())).willReturn(createResponse);

    String json = objectMapper.writeValueAsString(request);

    // when
    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders.post("/post")
            .characterEncoding("utf-8")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
    );

    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("post-create", getDocumentRequest(), getDocumentResponse(),
                requestFields(
                    fieldWithPath("title").description("????????? ??????"),
                    fieldWithPath("content").description("????????? ??????"),
                    fieldWithPath("imageUrl").description("????????? ????????? url"),
                    fieldWithPath("hashtags[]").description("???????????? ??????"),
                    fieldWithPath("creditType").description("?????? ??????"),
                    fieldWithPath("schedules.[].date").description("?????? ??????"),
                    fieldWithPath("schedules.[].startTime").description("?????? ?????? ??????"),
                    fieldWithPath("schedules.[].endTime").description("?????? ?????? ??????"),
                    fieldWithPath("schedules.[].region").description("?????? ??????"),
                    fieldWithPath("schedules.[].place").description("?????? ??????")
                ),
                responseFields(
                    fieldWithPath("status").description("?????? ??????"),
                    fieldWithPath("message").description("?????? ?????????"),
                    fieldWithPath("data.postId").description("????????? ID")
                ))
        );

  }

  @Test
  @DisplayName("????????? ??????")
  public void modifyPost() throws Exception {
    //given
    schedules.add(schedulesRequest);
    final PostDto.Request request =
        PostDto.Request.builder()
            .imageUrl("????????? ??????")
            .content("???????????? ???????????? ???????????????.")
            .title("????????? ??????????????????.")
            .creditType(CreditType.DIRECT)
            .schedules(schedules)
            .hashtags(hashtags)
            .build();

    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(session);

    given(postService.modifyPost(request, postId, session.getId())).willReturn(createResponse);

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
        .andDo(print())
        .andDo(
            document("post-modify", getDocumentRequest(), getDocumentResponse(),
                pathParameters(
                    parameterWithName("postId").description("????????? ID")
                ),
                requestFields(
                    fieldWithPath("title").description("????????? ??????"),
                    fieldWithPath("content").description("????????? ??????"),
                    fieldWithPath("imageUrl").description("????????? ????????? url"),
                    fieldWithPath("hashtags[]").description("???????????? ??????"),
                    fieldWithPath("creditType").description("?????? ??????"),
                    fieldWithPath("schedules.[].date").description("?????? ??????"),
                    fieldWithPath("schedules.[].startTime").description("?????? ?????? ??????"),
                    fieldWithPath("schedules.[].endTime").description("?????? ?????? ??????"),
                    fieldWithPath("schedules.[].region").description("?????? ??????"),
                    fieldWithPath("schedules.[].place").description("?????? ??????")
                ),
                responseFields(
                    fieldWithPath("status").description("?????? ??????"),
                    fieldWithPath("message").description("?????? ?????????"),
                    fieldWithPath("data.postId").description("????????? ID")
                ))
        );

  }

  @Test
  @DisplayName("????????? ??????")
  public void deletePost() throws Exception {
    // given
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(session);

    postService.deletePost(postId, session.getId());

    //when
    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .delete("/post/{postId}", postId)
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON)
    );

    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("post-delete", getDocumentRequest(), getDocumentResponse(),
                pathParameters(
                    parameterWithName("postId").description("????????? ID")
                ),
                responseFields(
                    fieldWithPath("status").description("?????? ??????"),
                    fieldWithPath("message").description("?????? ?????????")
                ))
        );

  }

  @Test
  @DisplayName("?????? ????????? ????????? ?????? ??????")
  void writerPost() throws Exception {

    List<PostDto.SearchResult> responses = new ArrayList<>();
    responses.add(post);

    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(session);

    given(postService.writerPost(session.getId())).willReturn(responses);

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .get("/post/writer")
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON)
    );

    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("post-writer-get", getDocumentRequest(), getDocumentResponse(),
                responseFields(
                    fieldWithPath("status").description("?????? ??????"),
                    fieldWithPath("message").description("?????? ?????????"),
                    fieldWithPath("data.[].postId").description("????????? ID"),
                    fieldWithPath("data.[].title").description("????????? ??????"),
                    fieldWithPath("data.[].content").description("????????? ??????"),
                    fieldWithPath("data.[].imageUrl").description("????????? ????????? url"),
                    fieldWithPath("data.[].hashtags[]").description("????????? ????????????")
                ))
        );

  }

}