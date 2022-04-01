package grooteogi.domain;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@Entity
public class UserInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(length = 10, nullable = false)
  private String name;

  @Column(length = 11)
  private String contact;

  @Column(length = 50)
  private String address;

  @Column(length = 45)
  private String collegeEmail;

  @CreationTimestamp
  private Timestamp registered;

  @OneToOne(mappedBy = "userInfo")
  private User user;
}
