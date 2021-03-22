package be.fcip.cms.persistence.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QAppParamEntity is a Querydsl query type for AppParamEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QAppParamEntity extends EntityPathBase<AppParamEntity> {

    private static final long serialVersionUID = 1142685831L;

    public static final QAppParamEntity appParamEntity = new QAppParamEntity("appParamEntity");

    public final StringPath id = createString("id");

    public final StringPath value = createString("value");

    public QAppParamEntity(String variable) {
        super(AppParamEntity.class, forVariable(variable));
    }

    public QAppParamEntity(Path<? extends AppParamEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAppParamEntity(PathMetadata metadata) {
        super(AppParamEntity.class, metadata);
    }

}

