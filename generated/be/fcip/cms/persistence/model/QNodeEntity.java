package be.fcip.cms.persistence.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNodeEntity is a Querydsl query type for NodeEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QNodeEntity extends EntityPathBase<NodeEntity> {

    private static final long serialVersionUID = -1509837027L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNodeEntity nodeEntity = new QNodeEntity("nodeEntity");

    public final MapPath<String, NodeContentEntity, QNodeContentEntity> contents = this.<String, NodeContentEntity, QNodeContentEntity>createMap("contents", String.class, NodeContentEntity.class, QNodeContentEntity.class);

    public final DateTimePath<java.time.LocalDateTime> created = createDateTime("created", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath includeBottom = createString("includeBottom");

    public final StringPath includeTop = createString("includeTop");

    public final SetPath<TaxonomyEntity, QTaxonomyEntity> taxonomies = this.<TaxonomyEntity, QTaxonomyEntity>createSet("taxonomies", TaxonomyEntity.class, QTaxonomyEntity.class, PathInits.DIRECT2);

    public final QNodeTypeEntity type;

    public final DateTimePath<java.time.LocalDateTime> updated = createDateTime("updated", java.time.LocalDateTime.class);

    public final QWebsiteEntity website;

    public QNodeEntity(String variable) {
        this(NodeEntity.class, forVariable(variable), INITS);
    }

    public QNodeEntity(Path<? extends NodeEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNodeEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNodeEntity(PathMetadata metadata, PathInits inits) {
        this(NodeEntity.class, metadata, inits);
    }

    public QNodeEntity(Class<? extends NodeEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.type = inits.isInitialized("type") ? new QNodeTypeEntity(forProperty("type")) : null;
        this.website = inits.isInitialized("website") ? new QWebsiteEntity(forProperty("website")) : null;
    }

}

