package grooteogi.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.sql.Timestamp;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(length = 200)
  private String message;

  private Boolean isCanceled;

  @CreationTimestamp
  private Timestamp createAt;

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "host_user_id")
  private User hostUser;

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "participate_user_id")
  private User participateUser;

  @OneToOne(cascade = {CascadeType.REMOVE})
  @JoinColumn(name = "schedule_id")
  private Schedule schedule;

}
