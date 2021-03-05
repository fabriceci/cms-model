package be.fcip.cms.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "page_content", indexes = {
        @Index(columnList = "computedSlug,language", unique = true)})


@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@EqualsAndHashCode(callSuper = true, of = {"id", "slug", "computedSlug", "enabled", "data", "version"})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageContentEntity extends AbstractTimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, updatable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;

    @Version
    private Long version;

    private String title;
    private String menuTitle;

    private String shortDescription;
    private String description;

    private String slug;
    @Column(unique = true)
    private String computedSlug;

    private boolean enabled = true;

    private String language;

    @Lob
    private String data;

    // WebContent
    private String intro;

    @ManyToOne
    @JoinColumn(name = "page_id")
    @JsonIgnore
    private PageEntity page;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pageContent", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    @OrderBy("position ASC")
    private List<PageFileEntity> contentFiles = new ArrayList<>();
}
