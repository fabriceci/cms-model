package be.fcip.cms.persistence.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QWordEntity is a Querydsl query type for WordEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QWordEntity extends EntityPathBase<WordEntity> {

    private static final long serialVersionUID = -1241454043L;

    public static final QWordEntity wordEntity = new QWordEntity("wordEntity");

    public final StringPath domain = createString("domain");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final MapPath<String, WordTranslationEntity, QWordTranslationEntity> translations = this.<String, WordTranslationEntity, QWordTranslationEntity>createMap("translations", String.class, WordTranslationEntity.class, QWordTranslationEntity.class);

    public final StringPath wordKey = createString("wordKey");

    public QWordEntity(String variable) {
        super(WordEntity.class, forVariable(variable));
    }

    public QWordEntity(Path<? extends WordEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWordEntity(PathMetadata metadata) {
        super(WordEntity.class, metadata);
    }

}

