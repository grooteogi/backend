package grooteogi.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
public class UserHashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @CreationTimestamp
    private Timestamp registered;

    @OneToOne(mappedBy = "userHashtag")
    private User user;

    @OneToOne(mappedBy = "userHashtag")
    private HashTag hashTag;
}
