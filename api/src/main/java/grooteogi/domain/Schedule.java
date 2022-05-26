package grooteogi.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import grooteogi.enums.RegionType;
import java.sql.Date;
import java.sql.Time;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(nullable = false, length = 10)
  private Date date;

  @Column(nullable = false, length = 10)
  private Time startTime;

  @Column(nullable = false, length = 10)
  private Time endTime;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private RegionType region;

  @Column(length = 40)
  private String place;

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "post_id")
  private Post post;

}
