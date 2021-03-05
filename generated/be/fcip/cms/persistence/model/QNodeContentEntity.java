package be.fcip.cms.persistence.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNodeContentEntity is a Querydsl query type for NodeContentEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QNodeContentEntity extends EntityPathBase<NodeContentEntity> {

    private static final long serialVersionUID = -57541310L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNodeContentEntity nodeContentEntity = new QNodeContentEntity("nodeContentEntity");

    public final DateTimePath<java.time.LocalDateTime> created = createDateTime("created", java.time.LocalDateTime.class);

    public final MapPath<String, Object, SimplePath<Object>> data = this.<String, Object, SimplePath<Object>>createMap("data", String.class, Object.class, SimplePath.class);

    public final StringPath description = createString("description");

    public final DateTimePath<java.time.LocalDateTime> displayFrom = createDateTime("displayFrom", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> displayUntil = createDateTime("displayUntil", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath language = createString("language");

    public final QNodeEntity node;

    public final StringPath seoDescription = createString("seoDescription");

    public final StringPath seoTitle = createString("seoTitle");

    public final StringPath shortDescription = createString("shortDescription");

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.LocalDateTime> updated = createDateTime("updated", java.time.LocalDateTime.class);

    public QNodeContentEntity(String variable) {
        this(NodeContentEntity.class, forVariable(variable), INITS);
    }

    public QNodeContentEntity(Path<? extends NodeContentEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNodeContentEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNodeContentEntity(PathMetadata metadata, PathInits inits) {
        this(NodeContentEntity.class, metadata, inits);
    }

    public QNodeContentEntity(Class<? extends NodeContentEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.node = inits.isInitialized("node") ? new QNodeEntity(forProperty("node"), inits.get("node")) : null;
    }

}

