package grooteogi.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
public class SnsInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 10, nullable = false)
    private String type;

    @Column(length = 255, nullable = false)
    private String token;

    @CreationTimestamp
    private Timestamp registered;

    @OneToOne(mappedBy = "snsInfo")
    private User user;
}

