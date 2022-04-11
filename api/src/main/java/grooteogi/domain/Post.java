package grooteogi.domain;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
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

  @Column(nullable = true, length = 125)
  private String imageUrl;

  @Column(length = 100, nullable = false, columnDefinition = "int default 0")
  private int views;

  @CreationTimestamp
  private Timestamp createDate;

  @CreationTimestamp
  private Timestamp modifiedDate;

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "user_id")
  private User user;


  @OneToMany(mappedBy = "post",
      cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
  @JsonManagedReference
  private List<PostHashtag> postHashtags = new ArrayList<>();

}