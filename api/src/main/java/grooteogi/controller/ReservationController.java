package grooteogi.controller;

import grooteogi.dto.ReservationDto;
import grooteogi.response.BasicResponse;
import grooteogi.service.ReservationService;
import grooteogi.utils.Session;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

  private final ReservationService reservationService;

  @GetMapping("/{reservationId}")
  public ResponseEntity<BasicResponse> getReservation(@PathVariable Integer reservationId) {
    ReservationDto.DetailResponse response = reservationService.getReservation(reservationId);
    return ResponseEntity.ok(BasicResponse.builder()
        .message("get reservation success").data(response).build());
  }

  @GetMapping
  public ResponseEntity<BasicResponse> getReservation(
      @RequestParam(name = "isHost") boolean isHost,
      @RequestParam(name = "filter", required = false) String filter)  {
    Session session = (Session) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    List<ReservationDto.DetailResponse> reservations
        = reservationService.getReservation(isHost, session.getId(), filter);

    return ResponseEntity.ok(BasicResponse.builder()
        .message("get reservation list with filtering success").data(reservations).build());
  }

  @PostMapping
  public ResponseEntity<BasicResponse> createReservation(
      @RequestBody ReservationDto.Request request) {
    Session session = (Session) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();

    ReservationDto.Response createdReservation = reservationService.createReservation(request,
        session.getId());
    return ResponseEntity.ok(BasicResponse.builder()
        .message("create reservation success").data(createdReservation).build());
  }

  @DeleteMapping("/{reservationId}")
  public ResponseEntity<BasicResponse> deleteReservation(@PathVariable Integer reservationId) {
    Session session = (Session) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();

    reservationService.deleteReservation(reservationId, session.getId());
    return ResponseEntity.ok(BasicResponse.builder()
        .message("delete reservation success").build());
  }

  @PatchMapping("/{reservationId}")
  public ResponseEntity<BasicResponse> modifyStatus(@PathVariable Integer reservationId) {
    Session session = (Session) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();

    ReservationDto.Response response =
        reservationService.modifyStatus(reservationId, session.getId());
    return ResponseEntity.ok(BasicResponse.builder()
        .message("modify reservation status success").data(response).build());
  }
  
  @PostMapping("/sms/send")
  public ResponseEntity<BasicResponse> sendSms(@RequestParam String phoneNumber) {
    this.reservationService.sendSms(phoneNumber);
    return ResponseEntity.ok(BasicResponse.builder()
        .message("send sms code success").build());
  }

  @PostMapping("/sms/check")
  public ResponseEntity<BasicResponse> checkSms(
      @RequestBody ReservationDto.CheckSmsRequest request) {
    reservationService.checkSms(request);

    return ResponseEntity.ok(
        BasicResponse.builder().message("check sms success").build());
  }
}
