package be.fcip.cms.persistence.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QCmsFieldEntity is a Querydsl query type for CmsFieldEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCmsFieldEntity extends EntityPathBase<CmsFieldEntity> {

    private static final long serialVersionUID = 937112332L;

    public static final QCmsFieldEntity cmsFieldEntity = new QCmsFieldEntity("cmsFieldEntity");

    public final BooleanPath array = createBoolean("array");

    public final StringPath codeName = createString("codeName");

    public final BooleanPath deletable = createBoolean("deletable");

    public final StringPath description = createString("description");

    public final StringPath hint = createString("hint");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath template = createString("template");

    public final StringPath type = createString("type");

    public QCmsFieldEntity(String variable) {
        super(CmsFieldEntity.class, forVariable(variable));
    }

    public QCmsFieldEntity(Path<? extends CmsFieldEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCmsFieldEntity(PathMetadata metadata) {
        super(CmsFieldEntity.class, metadata);
    }

}

