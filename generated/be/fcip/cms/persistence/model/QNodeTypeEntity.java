package be.fcip.cms.persistence.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNodeTypeEntity is a Querydsl query type for NodeTypeEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QNodeTypeEntity extends EntityPathBase<NodeTypeEntity> {

    private static final long serialVersionUID = -1116786185L;

    public static final QNodeTypeEntity nodeTypeEntity = new QNodeTypeEntity("nodeTypeEntity");

    public final StringPath fields = createString("fields");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final SetPath<NodeEntity, QNodeEntity> nodes = this.<NodeEntity, QNodeEntity>createSet("nodes", NodeEntity.class, QNodeEntity.class, PathInits.DIRECT2);

    public final BooleanPath useTaxonomy = createBoolean("useTaxonomy");

    public final BooleanPath useTimeLimit = createBoolean("useTimeLimit");

    public final BooleanPath useUrl = createBoolean("useUrl");

    public QNodeTypeEntity(String variable) {
        super(NodeTypeEntity.class, forVariable(variable));
    }

    public QNodeTypeEntity(Path<? extends NodeTypeEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNodeTypeEntity(PathMetadata metadata) {
        super(NodeTypeEntity.class, metadata);
    }

}

