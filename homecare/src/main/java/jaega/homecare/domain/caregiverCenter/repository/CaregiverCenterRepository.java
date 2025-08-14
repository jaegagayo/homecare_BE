package jaega.homecare.domain.caregiverCenter.repository;

import jaega.homecare.domain.caregiverCenter.entity.CaregiverCenter;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaregiverCenterRepository extends JpaRepository<CaregiverCenter, Long> {

    List<CaregiverCenter> findByStatus(CaregiverStatus status);
}
