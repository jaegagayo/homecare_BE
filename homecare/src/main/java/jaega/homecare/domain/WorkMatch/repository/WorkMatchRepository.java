package jaega.homecare.domain.WorkMatch.repository;

import jaega.homecare.domain.WorkMatch.entity.WorkMatch;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkMatchRepository extends JpaRepository<WorkMatch, Long> {
    Optional<WorkMatch> findByWorkMatchId(UUID workMatchId);

    List<WorkMatch> findByCaregiverOrderByIdDesc(Caregiver caregiver);
}
