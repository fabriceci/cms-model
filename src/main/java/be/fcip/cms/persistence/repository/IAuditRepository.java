package be.fcip.cms.persistence.repository;

import java.util.Date;
import java.util.List;

public interface IAuditRepository {

    List<Object[]> findAuditedExpired(Date maxDate, String tableName);
    void deleteAudits(List<Long> ids, String tableName);

    List<Number> getRevisionNumberList(Class requestedClass, long id);
    List<Number> getRevisionNumberList(Class requestedClass, String id);
    Object getRevisionEntity(Class requestedClass, Number id);
    Object[] getRevision(Class requestedClass, Number id);
}

