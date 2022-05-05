package grooteogi.controller;

import grooteogi.domain.Reservation;
import grooteogi.dto.ReservationDto;
import grooteogi.response.BasicResponse;
import grooteogi.service.ReservationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

  /*
  * 내가 호스트인 약속 리스트
  * */
  @GetMapping("/post")
  public ResponseEntity<BasicResponse> getPostReservation() {
    List<Reservation> reservationList = this.reservationService.getPostReservation();
    return ResponseEntity.ok(BasicResponse.builder().data(reservationList).build());
  }

  @GetMapping("/{reservationId}")
  public ResponseEntity<BasicResponse> getReservation(@PathVariable Integer reservationId) {
    Reservation reservation = this.reservationService.getReservation(reservationId);
    return ResponseEntity.ok(BasicResponse.builder().data(reservation).build());
  }

  /*
  * 내가 잡은 약속 리스트
  * */
  @GetMapping("/apply/{userId}")
  public ResponseEntity<BasicResponse> getUserReservation(@PathVariable Integer userId) {
    List<Reservation> reservations = reservationService.getUserReservation(userId);
    return ResponseEntity.ok(BasicResponse.builder().data(reservations).build());
  }

  @PostMapping
  public ResponseEntity<BasicResponse> createReservation(
      @RequestBody ReservationDto reservationDto) {
    Reservation createdReservation = this.reservationService.createReservation(reservationDto);
    return ResponseEntity.ok(BasicResponse.builder().data(createdReservation).build());
  }

  @DeleteMapping("/{reservationId}")
  public ResponseEntity<BasicResponse> deleteReservation(@PathVariable Integer reservationId) {
    this.reservationService.deleteReservation(reservationId);
    return ResponseEntity.ok(BasicResponse.builder().message("delete reservation success").build());
  }
}
