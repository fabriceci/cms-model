package be.fcip.cms.persistence.model;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_group")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name"})
@AllArgsConstructor
@NoArgsConstructor
public class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, updatable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false, columnDefinition = "TINYINT(1) default '1'")
    private boolean deletable;

    @OrderBy("section ASC, name ASC")
    @ManyToMany(fetch = FetchType.EAGER, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "group_to_permission",
            joinColumns = @JoinColumn(name = "user_group_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<PermissionEntity> permissions = new HashSet<>();

    @ManyToMany(mappedBy = "groups")
    private Set<UserEntity> users;
}
