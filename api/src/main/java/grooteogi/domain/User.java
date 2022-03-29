package grooteogi.domain;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(length = 40, nullable = false)
  private String nickname;

  @Column(length = 40, nullable = false)
  private String email;

  @CreationTimestamp
  private Timestamp modified;

  @CreationTimestamp
  private Timestamp registered;

  @OneToOne
  @JoinColumn(name = "id")
  private UserInfo userInfo;

  @OneToOne
  @JoinColumn(name = "id")
  private SnsInfo snsInfo;

}


