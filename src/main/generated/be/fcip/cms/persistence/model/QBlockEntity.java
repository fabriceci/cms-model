package be.fcip.cms.persistence.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBlockEntity is a Querydsl query type for BlockEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QBlockEntity extends EntityPathBase<BlockEntity> {

    private static final long serialVersionUID = 1706379064L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBlockEntity blockEntity = new QBlockEntity("blockEntity");

    public final StringPath content = createString("content");

    public final BooleanPath deletable = createBoolean("deletable");

    public final BooleanPath dynamic = createBoolean("dynamic");

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath language = createString("language");

    public final StringPath name = createString("name");

    public final StringPath type = createString("type");

    public final QWebsiteEntity website;

    public QBlockEntity(String variable) {
        this(BlockEntity.class, forVariable(variable), INITS);
    }

    public QBlockEntity(Path<? extends BlockEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBlockEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBlockEntity(PathMetadata metadata, PathInits inits) {
        this(BlockEntity.class, metadata, inits);
    }

    public QBlockEntity(Class<? extends BlockEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.website = inits.isInitialized("website") ? new QWebsiteEntity(forProperty("website")) : null;
    }

}

