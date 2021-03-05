package be.fcip.cms.persistence.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPageContentEntity is a Querydsl query type for PageContentEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPageContentEntity extends EntityPathBase<PageContentEntity> {

    private static final long serialVersionUID = 1434310101L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPageContentEntity pageContentEntity = new QPageContentEntity("pageContentEntity");

    public final QAbstractTimestampEntity _super = new QAbstractTimestampEntity(this);

    public final StringPath computedSlug = createString("computedSlug");

    public final ListPath<PageFileEntity, QPageFileEntity> contentFiles = this.<PageFileEntity, QPageFileEntity>createList("contentFiles", PageFileEntity.class, QPageFileEntity.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.util.Date> created = _super.created;

    public final StringPath data = createString("data");

    public final StringPath description = createString("description");

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath language = createString("language");

    public final StringPath menuTitle = createString("menuTitle");

    public final QPageEntity page;

    public final StringPath shortDescription = createString("shortDescription");

    public final StringPath slug = createString("slug");

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.util.Date> updated = _super.updated;

    public final NumberPath<Long> version = createNumber("version", Long.class);

    public QPageContentEntity(String variable) {
        this(PageContentEntity.class, forVariable(variable), INITS);
    }

    public QPageContentEntity(Path<? extends PageContentEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPageContentEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPageContentEntity(PathMetadata metadata, PathInits inits) {
        this(PageContentEntity.class, metadata, inits);
    }

    public QPageContentEntity(Class<? extends PageContentEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.page = inits.isInitialized("page") ? new QPageEntity(forProperty("page"), inits.get("page")) : null;
    }

}

