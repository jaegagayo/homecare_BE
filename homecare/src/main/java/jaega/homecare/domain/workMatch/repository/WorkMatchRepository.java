package jaega.homecare.domain.workMatch.repository;

import jaega.homecare.domain.workMatch.entity.WorkMatch;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface WorkMatchRepository extends JpaRepository<WorkMatch, Long> {
    Optional<WorkMatch> findByWorkMatchId(UUID workMatchId);

    List<WorkMatch> findByCaregiverAndWorkDateIn(Caregiver caregiver, Set<LocalDate> workDates);

}
