package be.fcip.cms.persistence.model;

import com.google.gson.annotations.Expose;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;

@Entity
@Table(name = "block")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name", "content"})
@NoArgsConstructor
@AllArgsConstructor
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class BlockEntity {

    @Expose
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, updatable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    @Column(unique=true)
    private String name;
    @Lob private String content;
    private String type;
    private boolean dynamic = false;
    private boolean enabled = true;
    private boolean deletable = true;
    private String language;
}
