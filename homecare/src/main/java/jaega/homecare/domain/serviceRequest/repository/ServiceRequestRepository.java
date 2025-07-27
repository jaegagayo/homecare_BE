package jaega.homecare.domain.serviceRequest.repository;

import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
}
