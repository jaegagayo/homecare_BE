package jaega.homecare.domain.serviceRequest.repository;

import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    Optional<ServiceRequest> findByServiceRequestId(UUID serviceRequestId);

    List<ServiceRequest> findAllByConsumerAndRequestStatus(Consumer consumer, ServiceRequestStatus requestStatus);

    List<ServiceRequest> findByConsumer(Consumer consumer);
}
