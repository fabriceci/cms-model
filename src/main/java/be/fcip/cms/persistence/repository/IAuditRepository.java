package be.fcip.cms.persistence.repository;

import java.util.Date;
import java.util.List;

public interface IAuditRepository {

    List<Object[]> findOldContentData(Date maxDate);

    List<Object[]> findOldBlockData(Date maxDate);

    void deleteOldContentData(List<Long> ids);

    List<Number> getRevisionNumberList(Class requestedClass, long id);
    List<Number> getRevisionNumberList(Class requestedClass, String id);
    Object getRevisionEntity(Class requestedClass, Number id);
    Object[] getRevision(Class requestedClass, Number id);

    void deleteOldBlock(List<Long> ids);
}

