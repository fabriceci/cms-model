package be.fcip.cms.persistence.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QPersistentLoginsEntity is a Querydsl query type for PersistentLoginsEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPersistentLoginsEntity extends EntityPathBase<PersistentLoginsEntity> {

    private static final long serialVersionUID = -2070487684L;

    public static final QPersistentLoginsEntity persistentLoginsEntity = new QPersistentLoginsEntity("persistentLoginsEntity");

    public final DateTimePath<java.util.Date> lastUsed = createDateTime("lastUsed", java.util.Date.class);

    public final StringPath series = createString("series");

    public final StringPath token = createString("token");

    public final StringPath username = createString("username");

    public QPersistentLoginsEntity(String variable) {
        super(PersistentLoginsEntity.class, forVariable(variable));
    }

    public QPersistentLoginsEntity(Path<? extends PersistentLoginsEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPersistentLoginsEntity(PathMetadata metadata) {
        super(PersistentLoginsEntity.class, metadata);
    }

}

