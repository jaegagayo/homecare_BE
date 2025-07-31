package jaega.homecare.domain.WorkLog.repository;

import jaega.homecare.domain.WorkLog.entity.WorkLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {
}
