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

  @GetMapping("/host")
  public ResponseEntity<BasicResponse> getHostReservation(
      @RequestParam(name = "sort", required = false) String sort)  {
    Session session = (Session) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();

    List<ReservationDto.Responses> reservationList = reservationService.getHostReservation(
        session.getId(), sort);
    return ResponseEntity.ok(BasicResponse.builder().data(reservationList).build());
  }

  @GetMapping("/{reservationId}")
  public ResponseEntity<BasicResponse> getReservation(@PathVariable Integer reservationId) {
    ReservationDto.Responses response = reservationService.getReservation(reservationId);
    return ResponseEntity.ok(BasicResponse.builder().data(response).build());
  }

  @GetMapping("/apply")
  public ResponseEntity<BasicResponse> getUserReservation(
      @RequestParam(name = "sort", required = false) String sort)  {
    Session session = (Session) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();

    List<ReservationDto.Responses> reservations = reservationService.getUserReservation(
        session.getId(), sort);
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

  @PatchMapping("/{reservationId}")
  public ResponseEntity<BasicResponse> modifyStatus(@PathVariable Integer reservationId) {
    ReservationDto.Response response = this.reservationService.modifyStatus(reservationId);
    return ResponseEntity.ok(BasicResponse.builder().data(response).build());
  }
}
