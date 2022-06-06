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
import grooteogi.controller.ReservationController;
import grooteogi.dto.ReservationDto;
import grooteogi.dto.ReservationDto.DetailResponse;
import grooteogi.service.ReservationService;
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
@WebMvcTest(ReservationController.class)
public class ReservationDocumentationTests {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private ReservationService reservationService;
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

  private final int reservationId = 1;
  final String[] hashtags = new String[]{"개발자", "코딩"};
  private final ReservationDto.DetailResponse response =
      ReservationDto.DetailResponse.builder()
          .reservationId(reservationId)
          .date("2022-05-25")
          .startTime("11:00:00")
          .endTime("12:00:00")
          .hashtags(List.of(hashtags))
          .imageUrl("imageUrl")
          .place("할리스")
          .region("마포구")
          .postId(1)
          .isCanceled(true)
          .title("개발이 좋아?")
          .applyPhone("01012345678")
          .hostPhone("01098765432")
          .text("잘부탁드려요.")
          .applyNickname("새내기")
          .review("리뷰 쓰기 싫어요")
          .score(2L)
          .build();

  private final ReservationDto.Response createResponse =
      ReservationDto.Response.builder()
          .reservationId(1)
          .build();
  private final String phoneNumber = "01012345678";

  @BeforeEach
  void setUp(WebApplicationContext webApplicationContext,
      RestDocumentationContextProvider restDocumentation) {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(
        documentationConfiguration(restDocumentation).operationPreprocessors()
            .withRequestDefaults(prettyPrint()).withResponseDefaults(prettyPrint())).build();
  }

  @Test
  @DisplayName("예약 개별 조회")
  public void getReservation() throws Exception {
    // given

    given(reservationService.getReservation(reservationId)).willReturn(response);

    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders
            .get("/reservation/{reservationId}", reservationId)
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON));
    result.andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("reservation-get", getDocumentRequest(), getDocumentResponse(),
                pathParameters(
                    parameterWithName("reservationId").description("예약 ID")
                ),
                responseFields(
                    fieldWithPath("status").description("결과 코드"),
                    fieldWithPath("message").description("응답 메세지"),
                    fieldWithPath("data.title").description("포스트 제목"),
                    fieldWithPath("data.reservationId").description("예약 ID"),
                    fieldWithPath("data.date").description("약속 날짜"),
                    fieldWithPath("data.startTime").description("약속 시작 시간"),
                    fieldWithPath("data.endTime").description("약속 종료 시간"),
                    fieldWithPath("data.region").description("약속 지역"),
                    fieldWithPath("data.place").description("약속 장소"),
                    fieldWithPath("data.hashtags[]").description("해시태그"),
                    fieldWithPath("data.postId").description("포스트 ID"),
                    fieldWithPath("data.imageUrl").description("포스트 이미지 url"),
                    fieldWithPath("data.isCanceled").description("약속 취소 여부"),
                    fieldWithPath("data.hostPhone").description("멘토 연락처"),
                    fieldWithPath("data.applyPhone").description("멘티 연락처"),
                    fieldWithPath("data.applyNickname").description("멘티 닉네임"),
                    fieldWithPath("data.text").description("호스트에게 남기는 말"),
                    fieldWithPath("data.review").description("리뷰 내용"),
                    fieldWithPath("data.score").description("리뷰 점수")
                )
            )
        );

  }

  @Test
  @DisplayName("호스트 예약 조회")
  void getHostReservation() throws Exception {

    final boolean isHost = true;
    final String filter = "PROCEED";

    List<DetailResponse> responses = new ArrayList<>();
    responses.add(response);

    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(session);

    given(reservationService.getReservation(isHost, session.getId(), filter)).willReturn(responses);

    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders
            .get("/reservation?isHost={isHost}&filter={filter}", isHost, filter)
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON));
    result.andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("reservation-get-host", getDocumentRequest(), getDocumentResponse(),
                requestParameters(
                    parameterWithName("isHost").description("멘토 여부"),
                    parameterWithName("filter").description("예약 조회 조건")
                ),
                responseFields(
                    fieldWithPath("status").description("결과 코드"),
                    fieldWithPath("message").description("응답 메세지"),
                    fieldWithPath("data.[].reservationId").description("예약 ID"),
                    fieldWithPath("data.[].title").description("포스트 제목"),
                    fieldWithPath("data.[].date").description("약속 날짜"),
                    fieldWithPath("data.[].startTime").description("약속 시작 시간"),
                    fieldWithPath("data.[].endTime").description("약속 종료 시간"),
                    fieldWithPath("data.[].region").description("약속 지역"),
                    fieldWithPath("data.[].place").description("약속 장소"),
                    fieldWithPath("data.[].hashtags[]").description("해시태그"),
                    fieldWithPath("data.[].postId").description("포스트 ID"),
                    fieldWithPath("data.[].imageUrl").description("포스트 이미지 url"),
                    fieldWithPath("data.[].isCanceled").description("약속 취소 여부"),
                    fieldWithPath("data.[].hostPhone").description("멘토 연락처"),
                    fieldWithPath("data.[].applyPhone").description("멘티 연락처"),
                    fieldWithPath("data.[].applyNickname").description("멘티 닉네임"),
                    fieldWithPath("data.[].text").description("호스트에게 남기는 말"),
                    fieldWithPath("data.[].review").description("리뷰 내용"),
                    fieldWithPath("data.[].score").description("리뷰 점수")
                )
            )
        );

  }

  @Test
  @DisplayName("참가자 예약 조회")
  void getApplyReservation() throws Exception {

    final boolean isHost = false;
    final String filter = "PROCEED";

    List<DetailResponse> responses = new ArrayList<>();
    responses.add(response);

    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(session);

    given(reservationService.getReservation(isHost, session.getId(), filter)).willReturn(responses);

    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders
            .get("/reservation?isHost={isHost}&filter={filter}", isHost, filter)
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON));
    result.andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("reservation-get-apply", getDocumentRequest(), getDocumentResponse(),
                requestParameters(
                    parameterWithName("isHost").description("멘토 여부"),
                    parameterWithName("filter").description("예약 조회 조건")
                ),
                responseFields(
                    fieldWithPath("status").description("결과 코드"),
                    fieldWithPath("message").description("응답 메세지"),
                    fieldWithPath("data.[].reservationId").description("예약 ID"),
                    fieldWithPath("data.[].title").description("포스트 제목"),
                    fieldWithPath("data.[].date").description("약속 날짜"),
                    fieldWithPath("data.[].startTime").description("약속 시작 시간"),
                    fieldWithPath("data.[].endTime").description("약속 종료 시간"),
                    fieldWithPath("data.[].region").description("약속 지역"),
                    fieldWithPath("data.[].place").description("약속 장소"),
                    fieldWithPath("data.[].hashtags[]").description("해시태그"),
                    fieldWithPath("data.[].postId").description("포스트 ID"),
                    fieldWithPath("data.[].imageUrl").description("포스트 이미지 url"),
                    fieldWithPath("data.[].isCanceled").description("약속 취소 여부"),
                    fieldWithPath("data.[].hostPhone").description("멘토 연락처"),
                    fieldWithPath("data.[].applyPhone").description("멘티 연락처"),
                    fieldWithPath("data.[].applyNickname").description("멘티 닉네임"),
                    fieldWithPath("data.[].text").description("호스트에게 남기는 말"),
                    fieldWithPath("data.[].review").description("리뷰 내용"),
                    fieldWithPath("data.[].score").description("리뷰 점수")
                )
            )
        );

  }


  @Test
  @DisplayName("예약생성")
  public void createReservation() throws Exception {
    // given
    final ReservationDto.Request request =
        ReservationDto.Request.builder()
            .scheduleId(1)
            .message("약속을 기다릴게요!")
            .build();

    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(session);

    given(reservationService.createReservation(request, session.getId())).willReturn(
        createResponse);

    String json = objectMapper.writeValueAsString(request);

    // when
    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders.post("/reservation")
            .characterEncoding("utf-8")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
    );

    // then
    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("reservation-create", getDocumentRequest(), getDocumentResponse(),
                requestFields(
                    fieldWithPath("scheduleId").description("스케쥴 ID"),
                    fieldWithPath("message").description("예약 시, 남기는 말")
                ),
                responseFields(
                    fieldWithPath("status").description("결과 코드"),
                    fieldWithPath("message").description("응답 메세지"),
                    fieldWithPath("data.reservationId").description("예약 ID")
                )
            )
        );

  }

  @Test
  @DisplayName("예약삭제")
  public void deleteReservation() throws Exception {
    //given

    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(session);

    reservationService.deleteReservation(reservationId, session.getId());

    //when
    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .delete("/reservation/{reservationId}", reservationId)
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON)
    );

    //then
    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("reservation-delete", getDocumentRequest(), getDocumentResponse(),
                pathParameters(
                    parameterWithName("reservationId").description("예약 ID")
                ),
                responseFields(
                    fieldWithPath("status").description("결과 코드"),
                    fieldWithPath("message").description("응답 메세지")
                )
            )
        );

  }

  @Test
  @DisplayName("예약 상태 변경")
  void modifyStatus() throws Exception {

    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(session);

    given(reservationService.modifyStatus(reservationId, session.getId())).willReturn(
        createResponse);

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .patch("/reservation/{reservationId}", reservationId)
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON)
    );

    //then
    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("reservation-modify", getDocumentRequest(), getDocumentResponse(),
                pathParameters(
                    parameterWithName("reservationId").description("예약 ID")
                ),
                responseFields(
                    fieldWithPath("status").description("결과 코드"),
                    fieldWithPath("message").description("응답 메세지"),
                    fieldWithPath("data.reservationId").description("예약 ID")
                )
            )
        );

  }

  @Test
  @DisplayName("연락처 문자 인증코드 전송")
  void sendSms() throws Exception {


    reservationService.sendSms(phoneNumber);
    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .post("/reservation/sms/send?phoneNumber={phoneNumber}", phoneNumber)

            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON)
    );

    //then
    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("reservation-send-sms", getDocumentRequest(), getDocumentResponse(),
                requestParameters(
                    parameterWithName("phoneNumber").description("연락처")
                ),
                responseFields(
                    fieldWithPath("status").description("결과 코드"),
                    fieldWithPath("message").description("응답 메세지")
                )
            )
        );

  }

  @Test
  @DisplayName("연락처 문자 인증코드 검사")
  void checkSms() throws Exception {

    final ReservationDto.CheckSmsRequest request =
        ReservationDto.CheckSmsRequest.builder()
            .code("ab12")
            .phoneNumber(phoneNumber)
            .build();

    reservationService.checkSms(request);

    String json = objectMapper.writeValueAsString(request);

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders
            .post("/reservation/sms/check")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON)
    );

    //then
    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(
            document("reservation-check-sms", getDocumentRequest(), getDocumentResponse(),
                requestFields(
                    fieldWithPath("phoneNumber").description("연락처"),
                    fieldWithPath("code").description("인증코드")
                ),
                responseFields(
                    fieldWithPath("status").description("결과 코드"),
                    fieldWithPath("message").description("응답 메세지")
                )
            )
        );

  }
}
