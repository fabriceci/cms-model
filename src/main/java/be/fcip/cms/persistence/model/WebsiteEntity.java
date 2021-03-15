package be.fcip.cms.persistence.model;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "website")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name"})
@NoArgsConstructor
@AllArgsConstructor
public class WebsiteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, updatable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;

    private String name;

    private String slug;

    private String baseUrl;

    private String image;

    @OneToOne(targetEntity = BlockEntity.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private BlockEntity master;

    @OneToMany(
            mappedBy = "website",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<PageEntity> pages;

    @OneToMany(
            mappedBy = "website",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<BlockEntity> blocks;

    @OneToMany(
            mappedBy = "website",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<PageTemplateEntity> templates;

    @ManyToMany
    @JoinTable(name = "users_websites",
            joinColumns = @JoinColumn(name = "website_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> users;
}
