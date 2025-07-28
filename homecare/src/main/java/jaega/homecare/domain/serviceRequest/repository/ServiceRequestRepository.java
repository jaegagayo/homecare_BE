package jaega.homecare.domain.serviceRequest.repository;

import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequestStatus;
import jaega.homecare.domain.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    Optional<ServiceRequest> findByServiceRequestId(UUID serviceRequestId);

    List<ServiceRequest> findAllByUserAndStatus(User user, ServiceRequestStatus status);

    List<ServiceRequest> findByUser(User user);
}
