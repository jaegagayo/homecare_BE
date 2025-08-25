package jaega.homecare.domain.caregiverPreference.repository;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiverPreference.entity.CaregiverPreference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaregiverPreferenceRepository extends JpaRepository<CaregiverPreference, Long> {
    CaregiverPreference findByCaregiver(Caregiver caregiver);
}
