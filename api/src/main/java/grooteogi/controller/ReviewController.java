package grooteogi.controller;

import grooteogi.dto.ReviewDto;
import grooteogi.response.BasicResponse;
import grooteogi.service.ReviewService;
import grooteogi.utils.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

  private final ReviewService reviewService;

  @PostMapping
  public ResponseEntity<BasicResponse> createReview(
      @RequestBody ReviewDto.Request request) {
    Session session = (Session) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();

    reviewService.createReview(request, session.getId());
    return ResponseEntity.ok(BasicResponse.builder().message("create review success").build());
  }

  @PatchMapping("/{reviewId}")
  public ResponseEntity<BasicResponse> modifyReview(
      @RequestBody ReviewDto.Request request, @PathVariable Integer reviewId) {
    Session session = (Session) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();

    reviewService.modifyReview(request, reviewId, session.getId());
    return ResponseEntity.ok(BasicResponse.builder().message("modify review success").build());
  }

  @DeleteMapping("/{reviewId}")
  public ResponseEntity<BasicResponse> deleteReview(@PathVariable Integer reviewId) {
    Session session = (Session) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();

    reviewService.deleteReview(reviewId, session.getId());
    return ResponseEntity.ok(BasicResponse.builder().message("delete review success").build());
  }
}
