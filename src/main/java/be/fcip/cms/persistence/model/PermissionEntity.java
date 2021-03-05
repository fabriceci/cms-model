package be.fcip.cms.persistence.model;

import lombok.*;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "permission")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id", "name"})
public class PermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, updatable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;

    @ManyToMany(mappedBy = "permissions")
    private Collection<GroupEntity> groups;

    @ManyToMany(mappedBy = "permissions")
    private Set<PageEntity> pages;

    private String name;

    @Column(nullable = false, columnDefinition = "TINYINT(1) default '0'")
    private boolean superAdmin;

    private String description;

    private String section;

    public PermissionEntity(final String name) {
        this.name = name;
    }
}
