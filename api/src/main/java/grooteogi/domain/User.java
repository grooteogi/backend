package grooteogi.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import grooteogi.enums.LoginType;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private LoginType type;

  @Column(length = 40, nullable = false)
  private String nickname = "groot";

  @Column(length = 255)
  private String password;

  @Column(length = 40, nullable = false)
  private String email;

  @CreationTimestamp
  private Timestamp createAt;

  @UpdateTimestamp
  private Timestamp updateAt;

  @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
  @JoinColumn(name = "user_info_id")
  private UserInfo userInfo;

  @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
  @JsonManagedReference
  private List<Post> posts = new ArrayList<>();

  @OneToMany(mappedBy = "hostUser", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
  @JsonManagedReference
  private List<Reservation> hostReserves = new ArrayList<>();

  @OneToMany(mappedBy = "participateUser", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
  @JsonManagedReference
  private List<Reservation> participateReserves = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
  @JsonManagedReference
  private List<Heart> hearts = new ArrayList<>();

}
