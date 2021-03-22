package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.repository.AuditRepositoryImpl;
import be.fcip.cms.persistence.repository.IAuditRepository;
import be.fcip.cms.util.CmsDateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@Slf4j
public class AuditServiceImpl implements IAuditService{

    @Autowired private IAuditRepository auditRepository;

    @Override
    public List<Number> getRevisionNumberList(Class requestedClass, Long id) {
        return auditRepository.getRevisionNumberList(requestedClass, id);
    }

    @Override
    public <T> T getRevisionEntity(Class<T> requestedClass, Number id){
        try {
            return requestedClass.cast(auditRepository.getRevisionEntity(requestedClass, id));
        } catch(Exception e){
            log.error("Revision Cast Exception, ID : " + id, e);
            return null;
        }
    }

    @Override
    public Object[] getRevision(Class requestedClass, Number id) {
        return auditRepository.getRevision(requestedClass, id);
    }

    @Override
    public void cleanAudit(){
        LocalDateTime dateTime = LocalDateTime.now();
        dateTime = dateTime.minusMonths(1L);
        Date maxDate = CmsDateUtils.LocalDateTimeToDate(dateTime);

        for (String tableName : AuditRepositoryImpl.AUDITED_TABLE) {
            List<Object[]> result = auditRepository.findAuditedExpired(maxDate, tableName);

            if(result != null && result.size() > 0) {
                List<Long> revIds = new ArrayList<>();
                for (Object[] o : result) {
                    revIds.add( ((Number)o[1]).longValue());
                }
                auditRepository.deleteAudits(revIds, tableName);
            }
        }
    }
}
