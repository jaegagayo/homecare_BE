package jaega.homecare.domain.settlement.repository;

import jaega.homecare.domain.caregiverCenter.entity.CaregiverCenter;
import jaega.homecare.domain.settlement.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    Optional<Settlement> findBySettlementId(UUID settlementId);

    List<Settlement> findByCaregiverCenter(CaregiverCenter caregiverCenter);

    List<Settlement> findByCaregiverCenter_Caregiver_CaregiverId(UUID caregiverId);
}
