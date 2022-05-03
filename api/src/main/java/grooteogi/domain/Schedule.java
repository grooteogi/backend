package grooteogi.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import grooteogi.enums.CreditType;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@Entity
public class Schedule {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(nullable = false, length = 10)
  private String startTime;

  @Column(nullable = false, length = 10)
  private String endTime;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CreditType credit;

  @Column(nullable = false, length = 10)
  private String region;

  @Column(length = 40)
  private String place;

  @CreationTimestamp
  private Timestamp createDate;

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "post_id")
  private Post post;

}
