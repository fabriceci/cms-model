package be.fcip.cms.persistence.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QModuleEntity is a Querydsl query type for ModuleEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QModuleEntity extends EntityPathBase<ModuleEntity> {

    private static final long serialVersionUID = 273543687L;

    public static final QModuleEntity moduleEntity = new QModuleEntity("moduleEntity");

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath installed = createBoolean("installed");

    public final StringPath name = createString("name");

    public final StringPath version = createString("version");

    public QModuleEntity(String variable) {
        super(ModuleEntity.class, forVariable(variable));
    }

    public QModuleEntity(Path<? extends ModuleEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QModuleEntity(PathMetadata metadata) {
        super(ModuleEntity.class, metadata);
    }

}

