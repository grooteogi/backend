package grooteogi;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import grooteogi.config.UserInterceptor;
import grooteogi.controller.ReservationController;
import grooteogi.domain.Post;
import grooteogi.domain.Reservation;
import grooteogi.domain.Schedule;
import grooteogi.domain.User;
import grooteogi.dto.ReservationDto;
import grooteogi.enums.CreditType;
import grooteogi.enums.LoginType;
import grooteogi.service.ReservationService;
import grooteogi.utils.JwtProvider;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(ReservationController.class)
public class ReservationControllerTest {

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

//  protected MockHttpSession session;

  User hostUser, particiUser;
  Post post;
  Schedule schedule;
  List<Schedule> schedules;

  @BeforeEach
  void setUp(WebApplicationContext webApplicationContext,
      RestDocumentationContextProvider restDocumentation) {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(
        documentationConfiguration(restDocumentation).operationPreprocessors()
            .withRequestDefaults(prettyPrint()).withResponseDefaults(prettyPrint())).build();

//    session = new MockHttpSession();
//    session.setAttribute("id", 1);
//    session.setAttribute("email", "groot22@example.com");

    // 예약을 위해 필요한 도메인
    hostUser = new User();
    hostUser.setId(1);
    hostUser.setType(LoginType.GENERAL);
    hostUser.setEmail("groot@example.com");
    hostUser.setPassword(passwordEncoder.encode("groot1234*"));
    hostUser.setNickname("groot-1");

    particiUser = new User();
    particiUser.setId(2);
    particiUser.setType(LoginType.GENERAL);
    particiUser.setEmail("groot22@example.com");
    particiUser.setPassword(passwordEncoder.encode("groot1234*"));
    particiUser.setNickname("groot-2");

    schedules = new ArrayList<>();
    schedule = new Schedule();
    schedule.setId(1);
    schedule.setDate("2022-05-07");
    schedule.setRegion("인천");
    schedule.setPlace("부평역");
    schedule.setStartTime("16:00");
    schedule.setEndTime("17:00");
    schedules.add(schedule);

    post = new Post();
    post.setId(1);
    post.setUser(hostUser);
    post.setPostHashtags(null);

    schedule.setPost(post);

    post.setSchedules(schedules);
    post.setViews(0);
    post.setTitle("제목이다.");
    post.setContent("내용이다");
    post.setCredit(CreditType.DIRECT);
    post.setImageUrl("이미지 주소다");
  }
  /*
   * given
   * 테스트를 위해 주어진 상태
   * 테스트 대상에게 주어진 조건
   * 테스트가 동작하기 위해 주어진 환경
   * when
   * 테스트 대상에게 가해진 어떠한 상태
   * 테스트 대상에게 주어진 어떠한 조건
   * 테스트 대상의 상태를 변경시키기 위한 환경
   * then
   * 앞선 과정의 결과
   * */

  @Test
  public void 예약생성() throws Exception {
    // given
    ReservationDto.Request request = reservationReq();
    Reservation response = getReservation();

    given(reservationService.createReservation(eq(request), anyInt())).willReturn(response);

    String json = objectMapper.writeValueAsString(request);

    // when
    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders.post("/reservation").characterEncoding("utf-8")
//            .session(session)
            .content(json)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
    );

    // then
    resultActions.andExpect(status().isOk())
        .andDo(print());

    verify(reservationService).createReservation(request, 1);
  }

//  @AfterEach
//  void clean() {
//    session.clearAttributes();
//  }

  private ReservationDto.Request reservationReq() {
    return ReservationDto.Request
        .builder()
        .message("msg")
        .scheduleId(1)
        .build();
  }

  private ReservationDto.Response reservationRes() {
    return ReservationDto.Response
        .builder()
        .reservationId(1)
        .date("2022-05-07")
        .endTime(null)
        .hashtags(null)
        .imgUrl(null)
        .place(null)
        .startTime(null)
        .title(null)
        .build();
  }

  private Reservation getReservation() {
    Reservation reservation = new Reservation();
    reservation.setId(1);
    reservation.setMessage("msg");
    reservation.setCreateDate(Timestamp.valueOf(LocalDateTime.now()));
    reservation.setHostUser(hostUser);
    reservation.setSchedule(schedule);
    reservation.setParticipateUser(particiUser);

    return reservation;
  }

    @Test
  public void 예약조회() throws Exception {
    // given
    Reservation reservation = getReservation();
    given(reservationService.getReservation(1)).willReturn(reservation);
    String reservationId = "1";

    ResultActions result = mockMvc.perform(
        RestDocumentationRequestBuilders.get("/reservation/{reservationId}", reservationId)
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON));
    result.andExpect(status().isOk())
        .andDo(print());
    verify(reservationService).getReservation(1);
  }

  @Test
  public void 예약삭제() throws Exception {
    //given
    int reservationId = anyInt();

    //when
    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders.delete("/reservation/{reservationId}", reservationId)
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON)
    );

    //then
    resultActions.andExpect(status().isOk())
        .andDo(print());

    verify(reservationService).deleteReservation(reservationId);
  }

  @Test
  public void 호스트유저예약조회() throws Exception {
    // given
    int hostUserId = anyInt();
    List<ReservationDto.Response> responses = new ArrayList<>();
    ReservationDto.Response response = reservationRes();
    responses.add(response);
    // when
    given(reservationService.getHostReservation(hostUserId)).willReturn(responses);

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders.get("/reservation/host").characterEncoding("uft-8")
            .accept(MediaType.APPLICATION_JSON)
    );

    // then
    resultActions.andExpect(status().isOk()).andDo(print());

    verify(reservationService).getHostReservation(hostUserId);

  }

  @Test
  public void 참가자유저예약조회() throws Exception {
    // given
    int particiUserId = anyInt();
    List<ReservationDto.Response> responses = new ArrayList<>();
    ReservationDto.Response response = reservationRes();
    responses.add(response);
    // when
    given(reservationService.getUserReservation(particiUserId)).willReturn(responses);

    ResultActions resultActions = mockMvc.perform(
        RestDocumentationRequestBuilders.get("/reservation/apply").characterEncoding("uft-8")
            .accept(MediaType.APPLICATION_JSON)
    );

    // then
    resultActions.andExpect(status().isOk()).andDo(print());

    verify(reservationService).getUserReservation(particiUserId);
  }


}
