package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.repository.IAuditRepository;
import be.fcip.cms.util.CmsDateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class SchedulerServiceImpl implements ISchedulerService {

    @Autowired
    private IAuditRepository auditRepository;

    @Autowired
    private IUserService userService;

    @Override
    public void cleanAudit() {

        LocalDateTime dateTime = LocalDateTime.now();
        dateTime = dateTime.minusMonths(1L);
        Date maxDate = CmsDateUtils.LocalDateTimeToDate(dateTime);

        // delete content data audit
        List<Object[]> result = auditRepository.findOldContentData(maxDate);

        if(result != null && result.size() > 0) {
            List<Long> revIds = new ArrayList<>();
            for (Object[] o : result) {
                revIds.add( ((Number)o[1]).longValue());
            }
            auditRepository.deleteOldContentData(revIds);
        }

        // delete content data audit
        result = auditRepository.findOldBlockData(maxDate);

        if(result != null && result.size() > 0) {
            List<Long> revIds = new ArrayList<>();
            for (Object[] o : result) {
                revIds.add( ((Number)o[1]).longValue());
            }
            auditRepository.deleteOldBlock(revIds);
        }
    }
}
