package jaega.homecare.domain.caregiver.repository;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.entity.CaregiverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CaregiverRepository extends JpaRepository<Caregiver, Long>{
    Optional<Caregiver> findByCaregiverId(UUID caregiverId);

    List<Caregiver> findAllByStatus(CaregiverStatus status);


    @Query("select c.id, st from Caregiver c join c.serviceTypes st where c.id in :caregiverIds")
    List<Object[]> findServiceTypesByIds(@Param("caregiverIds") Set<Long> caregiverIds);

    @Query("select c.id, st from Caregiver c join c.serviceTypes st where c.caregiverId in :caregiverIds")
    List<Object[]> findServiceTypesByCaregiverIds(@Param("caregiverIds") Set<UUID> caregiverIds);
}
