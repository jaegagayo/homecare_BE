package jaega.homecare.domain.caregiver.repository;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.entity.CaregiverStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CaregiverRepository extends JpaRepository<Caregiver, Long>{
    Optional<Caregiver> findByCaregiverId(UUID caregiverId);

    List<Caregiver> findAllByStatus(CaregiverStatus status);
}
