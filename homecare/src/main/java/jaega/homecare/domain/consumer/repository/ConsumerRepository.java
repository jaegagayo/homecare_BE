package jaega.homecare.domain.consumer.repository;

import jaega.homecare.domain.consumer.entity.Consumer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConsumerRepository extends JpaRepository<Consumer, Long> {
    Optional<Consumer> findByConsumerId(UUID consumerId);
}
