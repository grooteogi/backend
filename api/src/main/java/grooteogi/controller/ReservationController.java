package grooteogi.controller;

import grooteogi.domain.Reservation;
import grooteogi.dto.ReservationDto;
import grooteogi.dto.ReservationRes;
import grooteogi.response.BasicResponse;
import grooteogi.service.ReservationService;
import grooteogi.utils.Session;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {
  private final ReservationService reservationService;

  @GetMapping("/post")
  public ResponseEntity<BasicResponse> getPostReservation() {
    List<ReservationRes> reservationList = this.reservationService.getPostReservation();
    return ResponseEntity.ok(BasicResponse.builder().data(reservationList).build());
  }

  @GetMapping("/{reservationId}")
  public ResponseEntity<BasicResponse> getReservation(@PathVariable Integer reservationId) {
    Reservation reservation = this.reservationService.getReservation(reservationId);
    return ResponseEntity.ok(BasicResponse.builder().data(reservation).build());
  }

  @GetMapping("/apply")
  public ResponseEntity<BasicResponse> getUserReservation() {
    Session session = (Session) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    List<Reservation> reservations = reservationService.getUserReservation(session.getId());
    return ResponseEntity.ok(BasicResponse.builder().data(reservations).build());
  }

  @PostMapping
  public ResponseEntity<BasicResponse> createReservation(
      @RequestBody ReservationDto reservationDto) {
    Session session = (Session) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    Reservation createdReservation = reservationService
        .createReservation(reservationDto, session.getId());
    return ResponseEntity.ok(BasicResponse.builder().data(createdReservation).build());
  }

  @DeleteMapping("/{reservationId}")
  public ResponseEntity<BasicResponse> deleteReservation(@PathVariable Integer reservationId) {
    this.reservationService.deleteReservation(reservationId);
    return ResponseEntity.ok(BasicResponse.builder().message("delete reservation success").build());
  }
}
