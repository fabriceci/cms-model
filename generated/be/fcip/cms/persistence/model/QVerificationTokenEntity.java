package be.fcip.cms.persistence.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVerificationTokenEntity is a Querydsl query type for VerificationTokenEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QVerificationTokenEntity extends EntityPathBase<VerificationTokenEntity> {

    private static final long serialVersionUID = 904569481L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVerificationTokenEntity verificationTokenEntity = new QVerificationTokenEntity("verificationTokenEntity");

    public final DateTimePath<java.util.Date> expiryDate = createDateTime("expiryDate", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath token = createString("token");

    public final QUserEntity user;

    public QVerificationTokenEntity(String variable) {
        this(VerificationTokenEntity.class, forVariable(variable), INITS);
    }

    public QVerificationTokenEntity(Path<? extends VerificationTokenEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVerificationTokenEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVerificationTokenEntity(PathMetadata metadata, PathInits inits) {
        this(VerificationTokenEntity.class, metadata, inits);
    }

    public QVerificationTokenEntity(Class<? extends VerificationTokenEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUserEntity(forProperty("user"), inits.get("user")) : null;
    }

}

