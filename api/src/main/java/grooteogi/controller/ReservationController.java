package grooteogi.controller;

import grooteogi.dto.ReservationDto;
import grooteogi.response.BasicResponse;
import grooteogi.service.ReservationService;
import grooteogi.utils.Session;
import grooteogi.utils.SmsClient;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
  private final SmsClient smsClient;

  @GetMapping("/host")
  public ResponseEntity<BasicResponse> getHostReservation() {
    Session session = (Session) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();

    List<ReservationDto.Response> reservationList = reservationService.getHostReservation(
        session.getId());
    return ResponseEntity.ok(BasicResponse.builder().data(reservationList).build());
  }

  @GetMapping("/{reservationId}")
  public ResponseEntity<BasicResponse> getReservation(@PathVariable Integer reservationId) {
    ReservationDto.Response response = reservationService.getReservation(reservationId);
    return ResponseEntity.ok(BasicResponse.builder().data(response).build());
  }

  @GetMapping("/apply")
  public ResponseEntity<BasicResponse> getUserReservation() {
    Session session = (Session) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();

    List<ReservationDto.Response> reservations = reservationService.getUserReservation(
        session.getId());
    return ResponseEntity.ok(BasicResponse.builder().data(reservations).build());
  }

  @PostMapping
  public ResponseEntity<BasicResponse> createReservation(
      @RequestBody ReservationDto.Request request) {
    Session session = (Session) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();

    ReservationDto.Response createdReservation = reservationService.createReservation(request,
        session.getId());
    return ResponseEntity.ok(BasicResponse.builder().data(createdReservation).build());
  }

  @DeleteMapping("/{reservationId}")
  public ResponseEntity<BasicResponse> deleteReservation(@PathVariable Integer reservationId) {
    this.reservationService.deleteReservation(reservationId);
    return ResponseEntity.ok(BasicResponse.builder().message("delete reservation success").build());
  }

  @GetMapping("/send-sms")
  public ResponseEntity<BasicResponse> sendSms(@RequestParam String phoneNumber) {

    Random rand = new Random();
    StringBuilder numStr = new StringBuilder();
    for (int i = 0; i < 4; i++) {
      String ran = Integer.toString(rand.nextInt(10));
      numStr.append(ran);
    }

    smsClient.certifiedPhoneNumber(phoneNumber, numStr.toString());
    ReservationDto.SmsCode smsCode = ReservationDto.SmsCode.builder()
        .code(numStr.toString()).build();
    return ResponseEntity.ok(BasicResponse.builder().data(smsCode).build());
  }
}
