package be.fcip.cms.persistence.model;

import be.fcip.cms.util.ApplicationUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "page")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageEntity extends AbstractTimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, updatable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    private boolean enabled = true;

    private boolean menuItem = true;
    private boolean menuContentOnly  = false;
    private String menuClass;
    private String menuContent;

    @Column(name = "pos", nullable = false)
    private int position = -1;

    @Lob private String includeTop;
    @Lob private String includeBottom;

    @Column(columnDefinition = "TINYINT(1) default '0'")
    private boolean memberOnly = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private PageEntity pageParent;
    @OneToMany(mappedBy = "pageParent")
    @OrderBy("position ASC")
    private List<PageEntity> pageChildren = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "page", cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKeyColumn(name="language")
    private Map<String, PageContentEntity> contentMap = new HashMap<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_template_id")
    private PageTemplateEntity template;

    private String pageType;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(
            name = "page_to_permission",
            joinColumns = @JoinColumn(name = "page_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<PermissionEntity> permissions = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    //@OrderBy("position ASC")
    @JoinTable(name = "page_term",
            joinColumns = @JoinColumn(name = "page_id", referencedColumnName = "id") ,
            inverseJoinColumns = @JoinColumn(name = "taxonomy_id", referencedColumnName = "id"))
    private Set<TaxonomyEntity> taxonomyEntities = new HashSet<>();


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "website_id")
    private WebsiteEntity website;

    // Web Content
    private LocalDateTime beginDate;
    private LocalDateTime endDate;
    private String thumbnail;

    public PageEntity(Long id){
        this.id = id;
    }

    public void addContent(PageContentEntity pageContent){
        pageContent.setPage(this);
        contentMap.put(pageContent.getLanguage(), pageContent);
    }

    public void removeContentData(PageContentEntity pageContent) {
        pageContent.setPage(null);
        contentMap.remove(pageContent.getLanguage());

    }

    public String getName(){
        if(contentMap == null || contentMap.size() == 0) return null;
        if(contentMap.containsKey(ApplicationUtils.defaultLocale.toString()))
            return contentMap.get(ApplicationUtils.defaultLocale.toString()).getTitle();
        return contentMap.entrySet().iterator().next().getValue().getTitle();
    }
}
