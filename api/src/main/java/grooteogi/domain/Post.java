package grooteogi.domain;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@Entity
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(nullable = false, length = 30)
  private String title;

  @Lob
  private String content;

  @Column(nullable = false, length = 125)
  private String image;

  @ColumnDefault("0")
  private int count;

  @CreationTimestamp
  private Timestamp createDate;

  @ManyToOne
  @JsonManagedReference
  @JoinColumn(name = "post_user_id")
  private User user;

  @OneToMany
  @JsonManagedReference
  @JoinColumn(name = "post_hashtag")
  private PostHashtag postHashtag;

}