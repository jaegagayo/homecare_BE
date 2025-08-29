package jaega.homecare.domain.caregiver.repository;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CaregiverRepository extends JpaRepository<Caregiver, Long>{
    Optional<Caregiver> findByCaregiverId(UUID caregiverId);

    Caregiver findByUser(User user);
}
