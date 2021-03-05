package be.fcip.cms.persistence.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWebsiteEntity is a Querydsl query type for WebsiteEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QWebsiteEntity extends EntityPathBase<WebsiteEntity> {

    private static final long serialVersionUID = -1691932986L;

    public static final QWebsiteEntity websiteEntity = new QWebsiteEntity("websiteEntity");

    public final StringPath baseUrl = createString("baseUrl");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final ListPath<NodeEntity, QNodeEntity> nodes = this.<NodeEntity, QNodeEntity>createList("nodes", NodeEntity.class, QNodeEntity.class, PathInits.DIRECT2);

    public final ListPath<PageEntity, QPageEntity> pages = this.<PageEntity, QPageEntity>createList("pages", PageEntity.class, QPageEntity.class, PathInits.DIRECT2);

    public final ListPath<UserEntity, QUserEntity> users = this.<UserEntity, QUserEntity>createList("users", UserEntity.class, QUserEntity.class, PathInits.DIRECT2);

    public QWebsiteEntity(String variable) {
        super(WebsiteEntity.class, forVariable(variable));
    }

    public QWebsiteEntity(Path<? extends WebsiteEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWebsiteEntity(PathMetadata metadata) {
        super(WebsiteEntity.class, metadata);
    }

}

