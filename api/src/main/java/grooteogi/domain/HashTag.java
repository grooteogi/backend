package grooteogi.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
public class HashTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private Concern concern;

    @Enumerated(EnumType.STRING)
    private Personality personality;

    @Column(length = 200, nullable = false)
    private String tag;

    @CreationTimestamp
    private Timestamp registered;

    @OneToOne
    @JoinColumn(name = "id")
    private UserHashtag userHashtag;

}
