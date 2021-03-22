package be.fcip.cms.persistence.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTaxonomyEntity is a Querydsl query type for TaxonomyEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QTaxonomyEntity extends EntityPathBase<TaxonomyEntity> {

    private static final long serialVersionUID = 1466546476L;

    public static final QTaxonomyEntity taxonomyEntity = new QTaxonomyEntity("taxonomyEntity");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final ListPath<PageEntity, QPageEntity> pages = this.<PageEntity, QPageEntity>createList("pages", PageEntity.class, QPageEntity.class, PathInits.DIRECT2);

    public final NumberPath<Integer> position = createNumber("position", Integer.class);

    public final StringPath type = createString("type");

    public QTaxonomyEntity(String variable) {
        super(TaxonomyEntity.class, forVariable(variable));
    }

    public QTaxonomyEntity(Path<? extends TaxonomyEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTaxonomyEntity(PathMetadata metadata) {
        super(TaxonomyEntity.class, metadata);
    }

}

