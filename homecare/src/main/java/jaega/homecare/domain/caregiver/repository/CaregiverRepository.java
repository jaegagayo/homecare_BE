package jaega.homecare.domain.caregiver.repository;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CaregiverRepository extends JpaRepository<Caregiver, Long>{
    Optional<Caregiver> findByCaregiverId(UUID caregiverId);

    @Query("select c.caregiverId, st from Caregiver c join c.serviceTypes st where c.caregiverId in :caregiverIds")
    List<Object[]> findServiceTypesByCaregiverIds(@Param("caregiverIds") Set<UUID> caregiverIds);
}
