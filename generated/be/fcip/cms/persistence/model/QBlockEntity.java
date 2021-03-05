package be.fcip.cms.persistence.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QBlockEntity is a Querydsl query type for BlockEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QBlockEntity extends EntityPathBase<BlockEntity> {

    private static final long serialVersionUID = 1706379064L;

    public static final QBlockEntity blockEntity = new QBlockEntity("blockEntity");

    public final StringPath content = createString("content");

    public final BooleanPath deletable = createBoolean("deletable");

    public final BooleanPath dynamic = createBoolean("dynamic");

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath language = createString("language");

    public final StringPath name = createString("name");

    public final StringPath type = createString("type");

    public QBlockEntity(String variable) {
        super(BlockEntity.class, forVariable(variable));
    }

    public QBlockEntity(Path<? extends BlockEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBlockEntity(PathMetadata metadata) {
        super(BlockEntity.class, metadata);
    }

}

