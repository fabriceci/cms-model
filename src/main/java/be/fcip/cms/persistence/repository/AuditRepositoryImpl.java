package be.fcip.cms.persistence.repository;


import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuditRepositoryImpl implements IAuditRepository {

    @PersistenceContext(unitName = "core")
    private EntityManager entityManager;

    public final static String FIND_OLD_CONTENTDATA_QUERY = "SELECT c.id, c.rev, r.timestamp FROM page_content_AUD c JOIN REVINFO r ON r.id = c.rev WHERE r.timestamp < :max";
    public final static String FIND_OLD_BLOCK_QUERY = "SELECT c.id, c.rev, r.timestamp FROM block_AUD c JOIN REVINFO r ON r.id = c.rev WHERE r.timestamp < :max";
    public final static String DELETE_OLD_CONTENTDATA_QUERY = "DELETE FROM page_content_AUD WHERE REV IN (:ids)";
    public final static String DELETE_OLD_BLOCK_QUERY = "DELETE FROM block_AUD WHERE REV IN (:ids)";
    public final static String DELETE_REVINFO_QUERY = "DELETE FROM REVINFO WHERE id IN (:ids)";

    @Override
    public Object getRevisionEntity(Class requestedClass, Number id) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        return auditReader.createQuery()
                .forRevisionsOfEntity(requestedClass, true, false)
                .add(AuditEntity.revisionNumber().eq(id))
                .getSingleResult();
    }

    @Override
    public Object[] getRevision(Class requestedClass, Number id) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        AuditQuery query = auditReader.createQuery()
                .forRevisionsOfEntity(requestedClass, false, false)
                .add(AuditEntity.revisionNumber().eq(id));
        Object singleResult;
        try {
            singleResult = query.getSingleResult();
            return (Object[])singleResult;
        } catch(Exception e){
            log.error("Revision Exception during the cast, ID : " + id, e);
            return null;
        }
    }

    @Override
    public List<Number> getRevisionNumberList(Class requestedClass, long id) {

        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        List<Number> revisions = auditReader.getRevisions(requestedClass, id);
        if(revisions != null && !revisions.isEmpty()) revisions.remove(revisions.size() - 1 );
        return revisions;
    }

    @Override
    public List<Number> getRevisionNumberList(Class requestedClass, String id) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        List<Number> revisions = auditReader.getRevisions(requestedClass, id);
        if(revisions != null && !revisions.isEmpty()) revisions.remove(revisions.size() - 1 );
        return revisions;
    }

    @Override
    public List<Object[]> findOldContentData(Date maxDate) {
        Query q = entityManager.createNativeQuery(FIND_OLD_CONTENTDATA_QUERY);
        q.setParameter("max", maxDate.getTime());
        return q.getResultList();
    }

    @Override
    public void deleteOldContentData(List<Long> ids) {
        if(ids!= null && !ids.isEmpty()) {
            Query deleteAudit = entityManager.createNativeQuery(DELETE_OLD_CONTENTDATA_QUERY);
            executeDeleteAudit(deleteAudit, ids);
        }
    }

    @Override
    public List<Object[]> findOldBlockData(Date maxDate) {
        Query q = entityManager.createNativeQuery(FIND_OLD_BLOCK_QUERY);
        q.setParameter("max", maxDate.getTime());
        return q.getResultList();
    }

    @Override
    public void deleteOldBlock(List<Long> ids) {
        if(ids!= null && !ids.isEmpty()) {
            Query deleteBlock = entityManager.createNativeQuery(DELETE_OLD_BLOCK_QUERY);
            executeDeleteAudit(deleteBlock, ids);
        }
    }

    private void executeDeleteAudit(Query deleteAudit, List<Long> ids){String idStr = ids.stream().map(Object::toString).collect(Collectors.joining(","));
        Query revInfo = entityManager.createNativeQuery(DELETE_REVINFO_QUERY);

        deleteAudit.setParameter("ids", idStr);
        revInfo.setParameter("ids", idStr);

        deleteAudit.executeUpdate();
        revInfo.executeUpdate();
    }
}
