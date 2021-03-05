package be.fcip.cms.persistence.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPageFileEntity is a Querydsl query type for PageFileEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPageFileEntity extends EntityPathBase<PageFileEntity> {

    private static final long serialVersionUID = 1296402182L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPageFileEntity pageFileEntity = new QPageFileEntity("pageFileEntity");

    public final QAbstractTimestampEntity _super = new QAbstractTimestampEntity(this);

    //inherited
    public final DateTimePath<java.util.Date> created = _super.created;

    public final StringPath description = createString("description");

    public final BooleanPath enabled = createBoolean("enabled");

    public final StringPath extension = createString("extension");

    public final StringPath fileType = createString("fileType");

    public final StringPath groupName = createString("groupName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath mimeType = createString("mimeType");

    public final StringPath name = createString("name");

    public final QPageContentEntity pageContent;

    public final NumberPath<Integer> position = createNumber("position", Integer.class);

    public final StringPath serverName = createString("serverName");

    public final NumberPath<Long> size = createNumber("size", Long.class);

    //inherited
    public final DateTimePath<java.util.Date> updated = _super.updated;

    public QPageFileEntity(String variable) {
        this(PageFileEntity.class, forVariable(variable), INITS);
    }

    public QPageFileEntity(Path<? extends PageFileEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPageFileEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPageFileEntity(PathMetadata metadata, PathInits inits) {
        this(PageFileEntity.class, metadata, inits);
    }

    public QPageFileEntity(Class<? extends PageFileEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.pageContent = inits.isInitialized("pageContent") ? new QPageContentEntity(forProperty("pageContent"), inits.get("pageContent")) : null;
    }

}

