package grooteogi.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.sql.Timestamp;
import javax.persistence.Entity;
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
public class UserHashtag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @CreationTimestamp
  private Timestamp registered;

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "hashtag_id")
  private Hashtag hashtag;
}