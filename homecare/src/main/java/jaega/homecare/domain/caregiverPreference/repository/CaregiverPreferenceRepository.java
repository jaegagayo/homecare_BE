package jaega.homecare.domain.caregiverPreference.repository;

import jaega.homecare.domain.caregiverPreference.entity.CaregiverPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CaregiverPreferenceRepository extends JpaRepository<CaregiverPreference, Long> {
    Optional<CaregiverPreference> findByCaregiver_CaregiverId(UUID caregiverId);
}
