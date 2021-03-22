package be.fcip.cms.persistence.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPageEntity is a Querydsl query type for PageEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPageEntity extends EntityPathBase<PageEntity> {

    private static final long serialVersionUID = -1434841622L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPageEntity pageEntity = new QPageEntity("pageEntity");

    public final QAbstractTimestampEntity _super = new QAbstractTimestampEntity(this);

    public final DateTimePath<java.time.LocalDateTime> beginDate = createDateTime("beginDate", java.time.LocalDateTime.class);

    public final MapPath<String, PageContentEntity, QPageContentEntity> contentMap = this.<String, PageContentEntity, QPageContentEntity>createMap("contentMap", String.class, PageContentEntity.class, QPageContentEntity.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> created = _super.created;

    public final BooleanPath enabled = createBoolean("enabled");

    public final DateTimePath<java.time.LocalDateTime> endDate = createDateTime("endDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath includeBottom = createString("includeBottom");

    public final StringPath includeTop = createString("includeTop");

    public final BooleanPath memberOnly = createBoolean("memberOnly");

    public final StringPath menuClass = createString("menuClass");

    public final StringPath menuContent = createString("menuContent");

    public final BooleanPath menuContentOnly = createBoolean("menuContentOnly");

    public final BooleanPath menuItem = createBoolean("menuItem");

    public final ListPath<PageEntity, QPageEntity> pageChildren = this.<PageEntity, QPageEntity>createList("pageChildren", PageEntity.class, QPageEntity.class, PathInits.DIRECT2);

    public final QPageEntity pageParent;

    public final StringPath pageType = createString("pageType");

    public final SetPath<PermissionEntity, QPermissionEntity> permissions = this.<PermissionEntity, QPermissionEntity>createSet("permissions", PermissionEntity.class, QPermissionEntity.class, PathInits.DIRECT2);

    public final NumberPath<Integer> position = createNumber("position", Integer.class);

    public final SetPath<TaxonomyEntity, QTaxonomyEntity> taxonomyEntities = this.<TaxonomyEntity, QTaxonomyEntity>createSet("taxonomyEntities", TaxonomyEntity.class, QTaxonomyEntity.class, PathInits.DIRECT2);

    public final QPageTemplateEntity template;

    public final StringPath thumbnail = createString("thumbnail");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updated = _super.updated;

    public final QWebsiteEntity website;

    public QPageEntity(String variable) {
        this(PageEntity.class, forVariable(variable), INITS);
    }

    public QPageEntity(Path<? extends PageEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPageEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPageEntity(PathMetadata metadata, PathInits inits) {
        this(PageEntity.class, metadata, inits);
    }

    public QPageEntity(Class<? extends PageEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.pageParent = inits.isInitialized("pageParent") ? new QPageEntity(forProperty("pageParent"), inits.get("pageParent")) : null;
        this.template = inits.isInitialized("template") ? new QPageTemplateEntity(forProperty("template"), inits.get("template")) : null;
        this.website = inits.isInitialized("website") ? new QWebsiteEntity(forProperty("website")) : null;
    }

}

