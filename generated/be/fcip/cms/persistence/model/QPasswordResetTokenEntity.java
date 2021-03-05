package be.fcip.cms.persistence.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPasswordResetTokenEntity is a Querydsl query type for PasswordResetTokenEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPasswordResetTokenEntity extends EntityPathBase<PasswordResetTokenEntity> {

    private static final long serialVersionUID = 99884992L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPasswordResetTokenEntity passwordResetTokenEntity = new QPasswordResetTokenEntity("passwordResetTokenEntity");

    public final DateTimePath<java.util.Date> expiryDate = createDateTime("expiryDate", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath token = createString("token");

    public final QUserEntity user;

    public QPasswordResetTokenEntity(String variable) {
        this(PasswordResetTokenEntity.class, forVariable(variable), INITS);
    }

    public QPasswordResetTokenEntity(Path<? extends PasswordResetTokenEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPasswordResetTokenEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPasswordResetTokenEntity(PathMetadata metadata, PathInits inits) {
        this(PasswordResetTokenEntity.class, metadata, inits);
    }

    public QPasswordResetTokenEntity(Class<? extends PasswordResetTokenEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUserEntity(forProperty("user"), inits.get("user")) : null;
    }

}

