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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuditRepositoryImpl implements IAuditRepository {

    @PersistenceContext(unitName = "core")
    private EntityManager entityManager;

    private final static String FIND_OLD_AUD = "SELECT c.id, c.rev, r.timestamp FROM %s c JOIN REVINFO r ON r.id = c.rev WHERE r.timestamp < :max";
    private final static String DELETE_AUD = "DELETE FROM %s WHERE REV IN (:ids)";
    private final static String DELETE_REVINFO_QUERY = "DELETE FROM REVINFO WHERE id IN (:ids)";
    public final static List<String> AUDITED_TABLE = Arrays.asList("page_template_AUD", "page_content_AUD", "block_AUD", "website_AUD");


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
    public List<Object[]> findAuditedExpired(Date maxDate, String tableName) {
        Query q = entityManager.createNativeQuery(String.format(FIND_OLD_AUD, tableName));
        q.setParameter("max", maxDate.getTime());
        return q.getResultList();
    }

    @Override
    public void deleteAudits(List<Long> ids, String tableName) {
        if(ids!= null && !ids.isEmpty()) {
            Query deleteAudit = entityManager.createNativeQuery(String.format(DELETE_AUD, tableName));
            executeDeleteAudit(deleteAudit, ids);
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
