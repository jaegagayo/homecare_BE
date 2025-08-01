package jaega.homecare.domain.WorkLog.repository;

import jaega.homecare.domain.WorkLog.entity.WorkLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {
    Optional<WorkLog> findByWorkLogId(UUID workLogId);

}
