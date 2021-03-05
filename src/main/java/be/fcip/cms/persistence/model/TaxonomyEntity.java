package be.fcip.cms.persistence.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "taxonomy")
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class TaxonomyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, updatable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;

    private int position;

    private String type;
    private String name;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "taxonomyEntities")
    private List<PageEntity> pages;
}
