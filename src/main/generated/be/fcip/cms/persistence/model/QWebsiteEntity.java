package be.fcip.cms.persistence.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QWebsiteEntity is a Querydsl query type for WebsiteEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QWebsiteEntity extends EntityPathBase<WebsiteEntity> {

    private static final long serialVersionUID = -1691932986L;

    public static final QWebsiteEntity websiteEntity = new QWebsiteEntity("websiteEntity");

    public final StringPath baseUrl = createString("baseUrl");

    public final StringPath errorTemplate = createString("errorTemplate");

    public final BooleanPath https = createBoolean("https");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath image = createString("image");

    public final StringPath mailFrom = createString("mailFrom");

    public final StringPath mailName = createString("mailName");

    public final StringPath mailTo = createString("mailTo");

    public final BooleanPath maintenance = createBoolean("maintenance");

    public final StringPath name = createString("name");

    public final MapPath<String, String, StringPath> property = this.<String, String, StringPath>createMap("property", String.class, String.class, StringPath.class);

    public final StringPath slug = createString("slug");

    public final StringPath template = createString("template");

    public final MapPath<String, java.util.Map<String, String>, SimplePath<java.util.Map<String, String>>> translatableProperty = this.<String, java.util.Map<String, String>, SimplePath<java.util.Map<String, String>>>createMap("translatableProperty", String.class, java.util.Map.class, SimplePath.class);

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

