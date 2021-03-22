package be.fcip.cms.persistence.repository;

import be.fcip.cms.model.db.PageableResult;
import be.fcip.cms.persistence.model.PageEntity;
import be.fcip.cms.persistence.model.QPageContentEntity;
import be.fcip.cms.persistence.model.QPageEntity;
import be.fcip.cms.persistence.model.QTaxonomyEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

public class IPageRepositoryImpl implements IPageRepositoryCustom {
    @PersistenceContext(unitName = "core")
    private EntityManager entityManager;

    @Override
    public PageEntity findContentCustom(Long id) {
        QPageEntity PageEntity = QPageEntity.pageEntity;
        QPageContentEntity PageContentEntity = QPageContentEntity.pageContentEntity;
        QTaxonomyEntity taxonomyTermDataEntity = QTaxonomyEntity.taxonomyEntity;

        JPAQueryFactory queryFactor = new JPAQueryFactory(entityManager);

        PageEntity result = queryFactor.selectFrom(PageEntity)
                .leftJoin(PageEntity.contentMap, PageContentEntity).fetchJoin()
                .where(PageEntity.id.eq(id))
                //.leftJoin(PageEntity.dictionaryList).fetch()
                //.leftJoin(PageEntity.roles).fetchJoin()
                //.leftJoin(PageEntity.taxonomyTermEntities, taxonomyTermDataEntity).fetchJoin()
                //.leftJoin(taxonomyTermDataEntity.termDataList).fetch()
                .orderBy(PageEntity.position.asc(), PageEntity.pageChildren.any().position.asc())
                .fetchOne();

        // Lazy Load
        if(result != null) {
            Hibernate.initialize(result.getPermissions());
            Hibernate.initialize(result.getTaxonomyEntities());
            Hibernate.initialize(result.getPageChildren());
            if(result.getPageParent() != null)

                result.getContentMap().forEach((k, v) -> {
                    Hibernate.initialize(v.getContentFiles());
                    //for (FileEntity fileEntity : v.getContentFiles()) {
                    //  Hibernate.initialize(fileEntity.getFileParent());
                    //Hibernate.initialize(fileEntity.getRoles());
                    //Hibernate.initialize(fileEntity.getFileChildren());
                    //}
                });
        }

        return result;
    }

    @Override
    public PageableResult<PageEntity> findWebContentCustom(String locale, LocalDateTime begin, LocalDateTime end, String name, String type, String theme, String tags, String contentType, Long pageNumber, Long limit, Boolean isPrivate) {

        QPageEntity PageEntity = QPageEntity.pageEntity;
        QPageContentEntity PageContentEntity = QPageContentEntity.pageContentEntity;
        QTaxonomyEntity taxonomyType = new QTaxonomyEntity("taxonomyType");
        QTaxonomyEntity taxonomyTag = new QTaxonomyEntity("taxonomyTag");
        QTaxonomyEntity taxonomyTheme = new QTaxonomyEntity("taxonomyTheme");

        BooleanExpression themeBool = null;
        BooleanExpression typeBool = null;
        BooleanExpression tagBool = null;
        BooleanExpression tagSearchBool = null;

        JPAQuery query = new JPAQueryFactory(entityManager).selectFrom(PageEntity);

        BooleanBuilder builder = new BooleanBuilder();
        if (!StringUtils.isEmpty(name)) {
            // Special Feantsa
            if (StringUtils.isEmpty(tags)) {
                tagSearchBool = taxonomyTag.type.eq("TAG").and(taxonomyTag.name.in(name.split(" ")));
                builder.and(PageContentEntity.title.like("%" + name + "%").or(tagSearchBool));
            } else {
                builder.and(PageContentEntity.title.like("%" + name + "%"));
            }

        }
        if (begin != null) {
            if (end == null) end = LocalDateTime.now();
            builder.and(PageEntity.beginDate.between(begin, end));
        } else if (end != null) {
            builder.and(PageEntity.endDate.before(end));
        }else {
            builder.and(PageEntity.endDate.isNull().or(PageEntity.endDate.after(LocalDateTime.now())));
            builder.and(PageEntity.beginDate.isNull().or(PageEntity.beginDate.before(LocalDateTime.now())));
        }

        if (!StringUtils.isEmpty(contentType)) {
            builder.and(PageEntity.pageType.in(contentType.split(",")));
        }
        if (isPrivate != null) {
            builder.and(PageEntity.memberOnly.eq(isPrivate));
        }
        if (!StringUtils.isEmpty(theme)) {
            themeBool = taxonomyTheme.type.eq("THEME").and(taxonomyTheme.name.in(theme.split(",")));
        }
        //if (!StringUtils.isEmpty(type)) {
        //    typeBool = taxonomyType.type.eq("TYPE").and(taxonomyType.name.in(type.split(",")));
        //}
        if (!StringUtils.isEmpty(tags)) {
            tagBool = taxonomyTag.type.eq("TAG").and(taxonomyTag.name.in(tags.split(",")));
        }
        if (themeBool != null) {
            builder.and(themeBool);
        }
        if (tagBool != null) {
            builder.and(tagBool);
        }
        if (typeBool != null) {
            builder.and(typeBool);
        }

        if (!StringUtils.isEmpty(locale)) {
            builder.and(PageContentEntity.language.eq(locale));
        }

        query.leftJoin(PageEntity.contentMap, PageContentEntity).fetchJoin();
        if (themeBool != null)
            query.leftJoin(PageEntity.taxonomyEntities, taxonomyTheme);
        if (tagBool != null || tagSearchBool != null )
            query.leftJoin(PageEntity.taxonomyEntities, taxonomyTag);
        if (typeBool != null)
            query.leftJoin(PageEntity.taxonomyEntities, taxonomyType);

        query.where(builder).where(PageEntity.enabled.eq(true).and(PageContentEntity.enabled.eq(true)))
                .orderBy(PageEntity.beginDate.desc());

        if (limit != null && limit != 0) {
            query.limit(limit).offset((pageNumber - 1) * limit);
        }

        query.distinct();
        List<PageEntity> contents = query.fetch();

        PageableResult<PageEntity> pageableResult = new PageableResult<>();
        pageableResult.setResult(contents);
        pageableResult.setTotalResult(query.fetchCount());
        pageableResult.setCurrentPage(pageNumber);
        if (limit != null && limit != 0) {
            pageableResult.setTotalPage(((long) Math.ceil((pageableResult.getTotalResult() * 1.0) / limit)));
        } else{
            pageableResult.setTotalPage(1);
        }

        return pageableResult;
    }

    @Override
    public List<PageEntity> findRootsByPageIdCustom(Long contentId, String locale, boolean onlyMenuItem, Long websiteId) {
        QPageEntity PageEntity = QPageEntity.pageEntity;
        QPageContentEntity PageContentEntity = QPageContentEntity.pageContentEntity;

        // Roots or children
        List<PageEntity> result = null;

        JPAQuery resultQuery = new JPAQueryFactory(entityManager).selectFrom(PageEntity)
                .leftJoin(PageEntity.contentMap, PageContentEntity);

        BooleanBuilder queryBool = new BooleanBuilder();
        queryBool.and(PageEntity.enabled.eq(true));
        queryBool.and(PageEntity.pageType.like("PAGE%"));
        if(!StringUtils.isEmpty(locale)) {
            queryBool.and(PageContentEntity.language.eq(locale));
        }
        if(onlyMenuItem){
            queryBool.and(PageEntity.menuItem.eq(true));
        }
        if(contentId == null || contentId == 0L){
            queryBool.and(PageEntity.pageParent.isNull());
        } else {
            queryBool.and(PageEntity.pageParent.id.eq(contentId));
        }
        if(websiteId != null){
            queryBool.and(PageEntity.website.id.eq(websiteId));
        }

        resultQuery.where(queryBool);
        resultQuery.distinct();
        resultQuery.orderBy(PageEntity.position.asc());

        return resultQuery.fetch();
    }
}