package jaega.homecare.domain.workLog.repository;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.workLog.entity.WorkLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {
    Optional<WorkLog> findByWorkLogId(UUID workLogId);

    List<WorkLog> findByCaregiverAndWorkDateIn(Caregiver caregiver, Set<LocalDate> workDates);
}