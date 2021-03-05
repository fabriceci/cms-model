package be.fcip.cms.persistence.model;

import com.google.gson.annotations.Expose;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "cms_field")
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CmsFieldEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    @Expose
    private long id;
    @Expose
    private String name;

    @Column(unique = true, nullable = false)
    @Expose String codeName;

    @Expose
    private String description;
    @Expose
    private boolean array;
    @Expose
    private String type;
    @Expose
    private String hint;

    private boolean deletable = true;

    @OneToOne(targetEntity = BlockEntity.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private BlockEntity blockEntity;

}
