package be.fcip.cms.persistence.envers;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.*;

@Entity
@Table(name = "REVINFO")
@Getter
@Setter
@RevisionEntity(UserAndDateRevisionListener.class)
public class RevInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    private int id;

    private String username;

    private Long userId;

    @RevisionTimestamp
    private long timestamp;
}