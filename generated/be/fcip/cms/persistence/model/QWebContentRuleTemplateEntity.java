package be.fcip.cms.persistence.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QWebContentRuleTemplateEntity is a Querydsl query type for WebContentRuleTemplateEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QWebContentRuleTemplateEntity extends EntityPathBase<WebContentRuleTemplateEntity> {

    private static final long serialVersionUID = -1205174506L;

    public static final QWebContentRuleTemplateEntity webContentRuleTemplateEntity = new QWebContentRuleTemplateEntity("webContentRuleTemplateEntity");

    public final StringPath name = createString("name");

    public final NumberPath<Long> templateId = createNumber("templateId", Long.class);

    public QWebContentRuleTemplateEntity(String variable) {
        super(WebContentRuleTemplateEntity.class, forVariable(variable));
    }

    public QWebContentRuleTemplateEntity(Path<? extends WebContentRuleTemplateEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWebContentRuleTemplateEntity(PathMetadata metadata) {
        super(WebContentRuleTemplateEntity.class, metadata);
    }

}

