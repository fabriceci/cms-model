package be.fcip.cms.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "file")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {"id", "name", "description", "serverName"})
public class PageFileEntity extends AbstractTimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, updatable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;

    @Column(nullable = false, columnDefinition = "TINYINT(1) default '1'")
    private boolean enabled = true;

    @Column(nullable = false)
    private String name;
    private String description;
    private String mimeType;

    private String fileType;

    @Column(nullable = false)
    private int position = -1;

    @Column(nullable = false)
    private String extension;

    @Column(nullable = false)
    private String serverName;

    private String groupName;
    @Column(nullable = false, columnDefinition = "INT(11) UNSIGNED")

    private long size;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private PageContentEntity pageContent;
}
