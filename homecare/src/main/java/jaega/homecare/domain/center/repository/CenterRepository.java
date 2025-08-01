package jaega.homecare.domain.center.repository;

import jaega.homecare.domain.center.entity.Center;
import jaega.homecare.domain.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CenterRepository extends JpaRepository<Center, Long> {

    Optional<Center> findByCenterId(UUID centerId);

    Optional<Center> findByUser(User user);
}
