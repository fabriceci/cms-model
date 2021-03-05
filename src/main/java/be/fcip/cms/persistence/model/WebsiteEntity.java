package be.fcip.cms.persistence.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "website")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name"})
public class WebsiteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, updatable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;

    private String name;

    private String baseUrl;

    @OneToMany(
            mappedBy = "website",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PageEntity> pages;

    @OneToMany(
            mappedBy = "website",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<UserEntity> users;
}
