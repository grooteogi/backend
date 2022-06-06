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
  private final String[] hashtags = new String[]{"개발자", "코딩"};
  private final ScheduleDto.Request schedulesRequest =
      ScheduleDto.Request.builder()
          .date("2022-05-25")
          .startTime("12::00:00")
          .endTime("13:00:00")
          .place("할리스")
          .region("강서구")
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
          .imageUrl("유저 이미지 주소")
          .nickname("지나가는 개발자")
          .userId(1)
          .build();
  private final PostDto.Response response =
      PostDto.Response.builder()
          .createAt("2022-05-26")
          .imageUrl("포스트 이미지 주소")
          .content("포스트에 들어가는 내용입니다.")
          .title("포스트 제목이랍니다.")
          .creditType(CreditType.DIRECT)
          .hashtags(hashtags)
          .likes(likes)
          .mentor(mentor)
          .build();

  private final PostDto.SearchResult post =
      PostDto.SearchResult.builder()
          .postId(1)
          .hashtags(hashtags)
          .imageUrl("포스트 이미지 주소")
          .content("포스트에 들어가는 내용입니다.")
          .title("포스트 제목이랍니다.")
          .build();

  @BeforeEach
  void setUp(WebApplicationContext webApplicationContext,
      RestDocumentationContextProvider restDocumentation) {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(
        documentationConfiguration(restDocumentation).operationPreprocessors()
            .withRequestDefaults(prettyPrint()).withResponseDefaults(prettyPrint())).build();

  }

  @Test
  @DisplayName("포스트 검색")
  public void search() throws Exception {
    // given
    final String keyword = "포";
    final int page = 1;
    Pageable pages = PageRequest.of(page - 1, 12, Sort.by("id").descending());
    final String filter = "LATEST";
    final String region = "강서구";
    final List<SearchResult> posts = new ArrayList<>();

    posts.add(post);
    PostDto.SearchResponse response =
        PostDto.SearchResponse.builder()
            .posts(posts)
            .pageCount(1)
            .build();

    // when
    when(postService.search(keyword, filter, pages, region)).thenReturn(response);

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .get("/post/search?keyword=포&page=1&filter=LATEST&region=강서구")
    );
    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("post-search", getDocumentRequest(), getDocumentResponse(),
                requestParameters(
                    parameterWithName("keyword").description("검색어"),
                    parameterWithName("page").description("페이지 번호"),
                    parameterWithName("filter").description("검색 조건"),
                    parameterWithName("region").description("지역 조건")
                ),
                responseFields(
                    fieldWithPath("status").description("결과 코드"),
                    fieldWithPath("message").description("응답 메세지"),
                    fieldWithPath("data.posts[].postId").description("포스트 ID"),
                    fieldWithPath("data.posts[].title").description("포스트 제목"),
                    fieldWithPath("data.posts[].content").description("포스트 내용"),
                    fieldWithPath("data.posts[].imageUrl").description("포스트 이미지 url"),
                    fieldWithPath("data.posts[].hashtags[]").description("해시태그 이름"),
                    fieldWithPath("data['pageCount']").description("총 페이지 수")
                ))
        );

  }

  @Test
  @DisplayName("포스트 조회")
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
                    parameterWithName("postId").description("포스트 ID")
                ),
                responseFields(
                    fieldWithPath("status").description("결과 코드"),
                    fieldWithPath("message").description("응답 메세지"),
                    fieldWithPath("data.postId").description("포스트 ID"),
                    fieldWithPath("data.title").description("포스트 제목"),
                    fieldWithPath("data.content").description("포스트 내용"),
                    fieldWithPath("data.imageUrl").description("포스트 이미지 url"),
                    fieldWithPath("data.createAt").description("포스트 등록 일자"),
                    fieldWithPath("data.hashtags[]").description("해시태그 이름"),
                    fieldWithPath("data.creditType").description("결제 방식"),
                    fieldWithPath("data.likes.count").description("찜 수"),
                    fieldWithPath("data.likes.liked").description("찜 여부"),
                    fieldWithPath("data.mentor.userId").description("유저 ID"),
                    fieldWithPath("data.mentor.nickname").description("유저 닉네임"),
                    fieldWithPath("data.mentor.imageUrl").description("유저 이미지 url")
                ))
        );

  }

  @Test
  @DisplayName("스케쥴 조회")
  void getSchedules() throws Exception {

    List<ScheduleDto.Response> responses = new ArrayList<>();
    ScheduleDto.Response response =
        ScheduleDto.Response.builder()
            .date("2022-05-25")
            .startTime("12::00:00")
            .endTime("13:00:00")
            .place("할리스")
            .region("마포구")
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
                    parameterWithName("postId").description("포스트 ID")
                ),
                responseFields(
                    fieldWithPath("status").description("결과 코드"),
                    fieldWithPath("message").description("응답 메세지"),
                    fieldWithPath("data.[].scheduleId").description("스케쥴 ID"),
                    fieldWithPath("data.[].date").description("약속 일자"),
                    fieldWithPath("data.[].startTime").description("약속 시작 시간"),
                    fieldWithPath("data.[].endTime").description("약속 종료 시간"),
                    fieldWithPath("data.[].region").description("약속 지역"),
                    fieldWithPath("data.[].place").description("약속 장소")
                ))
        );

  }

  @Test
  @DisplayName("리뷰 조회")
  void getReview() throws Exception {

    List<ReviewDto.Response> responses = new ArrayList<>();
    ReviewDto.Response response =
        ReviewDto.Response.builder()
            .reviewId(1)
            .createAt("2022-05-25")
            .imageUrl("포스트 이미지 url")
            .nickname("지나가는 개발자")
            .score(5)
            .text("유익한 시간이었습니다.")
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
                    parameterWithName("postId").description("포스트 ID")
                ),
                responseFields(
                    fieldWithPath("status").description("결과 코드"),
                    fieldWithPath("message").description("응답 메세지"),
                    fieldWithPath("data.[].reviewId").description("리뷰 ID"),
                    fieldWithPath("data.[].score").description("평점"),
                    fieldWithPath("data.[].nickname").description("유저 닉네임"),
                    fieldWithPath("data.[].imageUrl").description("유저 이미지 url "),
                    fieldWithPath("data.[].createAt").description("리뷰 등록 일자"),
                    fieldWithPath("data.[].text").description("리뷰 글")
                ))
        );

  }

  @Test
  @DisplayName("찜 하기")
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
                    parameterWithName("postId").description("포스트 ID")
                ),
                responseFields(
                    fieldWithPath("status").description("결과 코드"),
                    fieldWithPath("message").description("응답 메세지")
                ))
        );

  }

  @Test
  @DisplayName("해시태그 조회")
  void getHashtags() throws Exception {

    List<HashtagDto.Response> responses = new ArrayList<>();
    HashtagDto.Response response =
        HashtagDto.Response.builder()
            .name("개발자")
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
                    parameterWithName("postId").description("포스트 ID")
                ),
                responseFields(
                    fieldWithPath("status").description("결과 코드"),
                    fieldWithPath("message").description("응답 메세지"),
                    fieldWithPath("data.[].name").description("해시태그 이름")
                ))
        );

  }

  @Test
  @DisplayName("포스트 생성")
  public void createPost() throws Exception {
    // given
    schedules.add(schedulesRequest);
    final PostDto.Request request =
        PostDto.Request.builder()
            .imageUrl("이미지 주소")
            .content("포스트에 들어가는 내용입니다.")
            .title("포스트 제목이랍니다.")
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
                    fieldWithPath("title").description("포스트 제목"),
                    fieldWithPath("content").description("포스트 내용"),
                    fieldWithPath("imageUrl").description("포스트 이미지 url"),
                    fieldWithPath("hashtags[]").description("해시태그 이름"),
                    fieldWithPath("creditType").description("결제 방식"),
                    fieldWithPath("schedules.[].date").description("약속 일자"),
                    fieldWithPath("schedules.[].startTime").description("약속 시작 시간"),
                    fieldWithPath("schedules.[].endTime").description("약속 종료 시간"),
                    fieldWithPath("schedules.[].region").description("약속 지역"),
                    fieldWithPath("schedules.[].place").description("약속 장소")
                ),
                responseFields(
                    fieldWithPath("status").description("결과 코드"),
                    fieldWithPath("message").description("응답 메세지"),
                    fieldWithPath("data.postId").description("포스트 ID")
                ))
        );

  }

  @Test
  @DisplayName("포스트 수정")
  public void modifyPost() throws Exception {
    //given
    schedules.add(schedulesRequest);
    final PostDto.Request request =
        PostDto.Request.builder()
            .imageUrl("이미지 주소")
            .content("포스트에 들어가는 내용입니다.")
            .title("포스트 제목이랍니다.")
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
                    parameterWithName("postId").description("포스트 ID")
                ),
                requestFields(
                    fieldWithPath("title").description("포스트 제목"),
                    fieldWithPath("content").description("포스트 내용"),
                    fieldWithPath("imageUrl").description("포스트 이미지 url"),
                    fieldWithPath("hashtags[]").description("해시태그 이름"),
                    fieldWithPath("creditType").description("결제 방식"),
                    fieldWithPath("schedules.[].date").description("약속 일자"),
                    fieldWithPath("schedules.[].startTime").description("약속 시작 시간"),
                    fieldWithPath("schedules.[].endTime").description("약속 종료 시간"),
                    fieldWithPath("schedules.[].region").description("약속 지역"),
                    fieldWithPath("schedules.[].place").description("약속 장소")
                ),
                responseFields(
                    fieldWithPath("status").description("결과 코드"),
                    fieldWithPath("message").description("응답 메세지"),
                    fieldWithPath("data.postId").description("포스트 ID")
                ))
        );

  }

  @Test
  @DisplayName("포스트 삭제")
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
                    parameterWithName("postId").description("포스트 ID")
                ),
                responseFields(
                    fieldWithPath("status").description("결과 코드"),
                    fieldWithPath("message").description("응답 메세지")
                ))
        );

  }

  @Test
  @DisplayName("내가 작성한 포스트 목록 조회")
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
                    fieldWithPath("status").description("결과 코드"),
                    fieldWithPath("message").description("응답 메세지"),
                    fieldWithPath("data.[].postId").description("포스트 ID"),
                    fieldWithPath("data.[].title").description("포스트 제목"),
                    fieldWithPath("data.[].content").description("포스트 내용"),
                    fieldWithPath("data.[].imageUrl").description("포스트 이미지 url"),
                    fieldWithPath("data.[].hashtags[]").description("포스트 해시태그")
                ))
        );

  }

}