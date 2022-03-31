package grooteogi.domain;

import grooteogi.enums.HashtagType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Entity
public class Hashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HashtagType hashtagType; //concern, personality

    @Column(length = 200, nullable = false)
    private String tag;

    @CreationTimestamp
    private Timestamp registered;

    @OneToMany(mappedBy = "hashTag")
    private List<UserHashtag> userHashtag;

}
