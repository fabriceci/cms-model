package be.fcip.cms.persistence.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserEntity is a Querydsl query type for UserEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QUserEntity extends EntityPathBase<UserEntity> {

    private static final long serialVersionUID = -1129858522L;

    public static final QUserEntity userEntity = new QUserEntity("userEntity");

    public final QAbstractTimestampEntity _super = new QAbstractTimestampEntity(this);

    public final BooleanPath accountNonExpired = createBoolean("accountNonExpired");

    public final BooleanPath accountNonLocked = createBoolean("accountNonLocked");

    public final StringPath avatar = createString("avatar");

    public final DatePath<java.time.LocalDate> birthday = createDate("birthday", java.time.LocalDate.class);

    public final StringPath city = createString("city");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> created = _super.created;

    public final BooleanPath credentialsNonExpired = createBoolean("credentialsNonExpired");

    public final StringPath email = createString("email");

    public final BooleanPath enabled = createBoolean("enabled");

    public final StringPath firstName = createString("firstName");

    public final EnumPath<UserEntity.Gender> gender = createEnum("gender", UserEntity.Gender.class);

    public final SetPath<GroupEntity, QGroupEntity> groups = this.<GroupEntity, QGroupEntity>createSet("groups", GroupEntity.class, QGroupEntity.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath lastName = createString("lastName");

    public final StringPath organisation = createString("organisation");

    public final StringPath password = createString("password");

    public final DateTimePath<java.time.LocalDateTime> passwordModifiedDate = createDateTime("passwordModifiedDate", java.time.LocalDateTime.class);

    public final StringPath street1 = createString("street1");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updated = _super.updated;

    public final SetPath<WebsiteEntity, QWebsiteEntity> website = this.<WebsiteEntity, QWebsiteEntity>createSet("website", WebsiteEntity.class, QWebsiteEntity.class, PathInits.DIRECT2);

    public final StringPath zip = createString("zip");

    public QUserEntity(String variable) {
        super(UserEntity.class, forVariable(variable));
    }

    public QUserEntity(Path<? extends UserEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserEntity(PathMetadata metadata) {
        super(UserEntity.class, metadata);
    }

}

