package be.fcip.cms.persistence.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPageTemplateEntity is a Querydsl query type for PageTemplateEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPageTemplateEntity extends EntityPathBase<PageTemplateEntity> {

    private static final long serialVersionUID = 715000836L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPageTemplateEntity pageTemplateEntity = new QPageTemplateEntity("pageTemplateEntity");

    public final BooleanPath active = createBoolean("active");

    public final BooleanPath deletable = createBoolean("deletable");

    public final StringPath description = createString("description");

    public final BooleanPath dynamicUrl = createBoolean("dynamicUrl");

    public final StringPath fields = createString("fields");

    public final BooleanPath fullCache = createBoolean("fullCache");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath includeBottom = createString("includeBottom");

    public final StringPath includeTop = createString("includeTop");

    public final StringPath name = createString("name");

    public final SetPath<PageEntity, QPageEntity> pages = this.<PageEntity, QPageEntity>createSet("pages", PageEntity.class, QPageEntity.class, PathInits.DIRECT2);

    public final BooleanPath shortCache = createBoolean("shortCache");

    public final StringPath template = createString("template");

    public final StringPath type = createString("type");

    public final BooleanPath useFiles = createBoolean("useFiles");

    public final BooleanPath useGallery = createBoolean("useGallery");

    public final BooleanPath useH1Field = createBoolean("useH1Field");

    public final QWebsiteEntity website;

    public QPageTemplateEntity(String variable) {
        this(PageTemplateEntity.class, forVariable(variable), INITS);
    }

    public QPageTemplateEntity(Path<? extends PageTemplateEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPageTemplateEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPageTemplateEntity(PathMetadata metadata, PathInits inits) {
        this(PageTemplateEntity.class, metadata, inits);
    }

    public QPageTemplateEntity(Class<? extends PageTemplateEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.website = inits.isInitialized("website") ? new QWebsiteEntity(forProperty("website")) : null;
    }

}

