package jaega.homecare.domain.recurringOffer.repository;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.recurringOffer.entity.RecurringOffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecurringOfferRepository extends JpaRepository<RecurringOffer, Long> {
    Optional<RecurringOffer> findByRecurringOfferId(UUID recurringOfferId);

    List<RecurringOffer> findByConsumer(Consumer consumer);

    List<RecurringOffer> findByCaregiver(Caregiver caregiver);
}
