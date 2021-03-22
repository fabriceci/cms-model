package be.fcip.cms.persistence.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPermissionEntity is a Querydsl query type for PermissionEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPermissionEntity extends EntityPathBase<PermissionEntity> {

    private static final long serialVersionUID = 979218218L;

    public static final QPermissionEntity permissionEntity = new QPermissionEntity("permissionEntity");

    public final StringPath description = createString("description");

    public final CollectionPath<GroupEntity, QGroupEntity> groups = this.<GroupEntity, QGroupEntity>createCollection("groups", GroupEntity.class, QGroupEntity.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final SetPath<PageEntity, QPageEntity> pages = this.<PageEntity, QPageEntity>createSet("pages", PageEntity.class, QPageEntity.class, PathInits.DIRECT2);

    public final StringPath section = createString("section");

    public final BooleanPath superAdmin = createBoolean("superAdmin");

    public QPermissionEntity(String variable) {
        super(PermissionEntity.class, forVariable(variable));
    }

    public QPermissionEntity(Path<? extends PermissionEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPermissionEntity(PathMetadata metadata) {
        super(PermissionEntity.class, metadata);
    }

}

