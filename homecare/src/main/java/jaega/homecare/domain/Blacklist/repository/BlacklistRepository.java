package jaega.homecare.domain.Blacklist.repository;

import jaega.homecare.domain.Blacklist.entity.Blacklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BlacklistRepository extends JpaRepository<Blacklist, Long> {

    Optional<Blacklist> findByBlacklistId(UUID caregiverBlacklistId);

    List<Blacklist> findByConsumer_ConsumerId(UUID consumerId);
}