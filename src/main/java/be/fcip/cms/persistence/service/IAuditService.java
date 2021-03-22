package be.fcip.cms.persistence.service;

import java.util.List;

public interface IAuditService {



    List<Number> getRevisionNumberList(Class requestedClass, Long id);

    <T> T getRevisionEntity(Class<T> requestedClass, Number id);

    abstract Object[] getRevision(Class requestedClass, Number id);

    void cleanAudit();
}
