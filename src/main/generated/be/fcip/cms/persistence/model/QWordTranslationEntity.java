package be.fcip.cms.persistence.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWordTranslationEntity is a Querydsl query type for WordTranslationEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QWordTranslationEntity extends EntityPathBase<WordTranslationEntity> {

    private static final long serialVersionUID = -517466510L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWordTranslationEntity wordTranslationEntity = new QWordTranslationEntity("wordTranslationEntity");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath language = createString("language");

    public final StringPath value = createString("value");

    public final QWordEntity word;

    public QWordTranslationEntity(String variable) {
        this(WordTranslationEntity.class, forVariable(variable), INITS);
    }

    public QWordTranslationEntity(Path<? extends WordTranslationEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWordTranslationEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWordTranslationEntity(PathMetadata metadata, PathInits inits) {
        this(WordTranslationEntity.class, metadata, inits);
    }

    public QWordTranslationEntity(Class<? extends WordTranslationEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.word = inits.isInitialized("word") ? new QWordEntity(forProperty("word")) : null;
    }

}

