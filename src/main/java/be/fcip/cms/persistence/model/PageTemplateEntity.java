package be.fcip.cms.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "page_template")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class PageTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, updatable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Lob private String includeTop;
    @Lob private String includeBottom;

    boolean fullCache = true;
    boolean shortCache = false;
    boolean useH1Field = true;
    boolean useFiles = false;
    boolean useGallery = false;

    private String description;

    private boolean active = true;

    private String type;
    private boolean deletable = true;
    private boolean dynamicUrl = false;

    @Lob private String fields;

    @Lob
    private String template;

    @OneToMany(
            mappedBy = "template",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @NotAudited
    private Set<PageEntity> pages = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "website_id")
    private WebsiteEntity website;
}
