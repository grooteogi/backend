package grooteogi.domain;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private LoginType type; //일반:0 카카오:1 페북:2

  @Column(length = 40, nullable = false)
  private String nickname;

  @Column(length = 255, nullable = false)
  private String password;

  @Column(length = 40, nullable = false)
  private String email;

  @CreationTimestamp
  private Timestamp modified;

  @CreationTimestamp
  private Timestamp registered;

  @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
  @JoinColumn(name = "user_info_id")
  private UserInfo userInfo;

  @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
  private List<UserHashtag> userHashtag = new ArrayList<>();
}