package grooteogi.domain;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import grooteogi.enums.CreditType;
import java.sql.Timestamp;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@AllArgsConstructor
@NoArgsConstructor
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(nullable = false, length = 30)
  private String title;

  @Lob
  private String content;

  @Column(nullable = true, length = 125)
  private String imageUrl;

  @Column(length = 100, nullable = false, columnDefinition = "int default 0")
  private int views;

  @CreationTimestamp
  private Timestamp createAt;

  @UpdateTimestamp
  private Timestamp updateAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CreditType credit;

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "user_id")
  private User user;

  @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST,
      CascadeType.REMOVE}, fetch = FetchType.LAZY)
  @JsonManagedReference
  private List<PostHashtag> postHashtags;

  @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST,
      CascadeType.REMOVE}, fetch = FetchType.LAZY)
  @JsonManagedReference
  private List<Schedule> schedules;

  @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST,
      CascadeType.REMOVE}, fetch = FetchType.LAZY)
  @JsonManagedReference
  private List<Heart> hearts;

  @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST,
    CascadeType.REMOVE}, fetch = FetchType.LAZY)
  @JsonManagedReference
  private List<Review> reviews;
}
