package be.fcip.cms.persistence.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QAbstractTimestampEntity is a Querydsl query type for AbstractTimestampEntity
 */
@Generated("com.querydsl.codegen.SupertypeSerializer")
public class QAbstractTimestampEntity extends EntityPathBase<AbstractTimestampEntity> {

    private static final long serialVersionUID = 397296767L;

    public static final QAbstractTimestampEntity abstractTimestampEntity = new QAbstractTimestampEntity("abstractTimestampEntity");

    public final DateTimePath<java.util.Date> created = createDateTime("created", java.util.Date.class);

    public final DateTimePath<java.util.Date> updated = createDateTime("updated", java.util.Date.class);

    public QAbstractTimestampEntity(String variable) {
        super(AbstractTimestampEntity.class, forVariable(variable));
    }

    public QAbstractTimestampEntity(Path<? extends AbstractTimestampEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAbstractTimestampEntity(PathMetadata metadata) {
        super(AbstractTimestampEntity.class, metadata);
    }

}

