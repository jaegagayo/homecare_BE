package jaega.homecare.domain.caregiverCenter.repository;

import jaega.homecare.domain.caregiverCenter.entity.CaregiverCenter;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CaregiverCenterRepository extends JpaRepository<CaregiverCenter, Long> {
    Optional<CaregiverCenter> findByCaregiverCenterId(UUID caregiverCenterId);

    List<CaregiverCenter> findByStatus(CaregiverStatus status);

    Optional<CaregiverCenter> findByCaregiverCenterIdAndStatus(UUID caregiverCenterId, CaregiverStatus status);
}
