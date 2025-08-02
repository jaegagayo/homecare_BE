package jaega.homecare.domain.serviceRequest.service.command;

import jaega.homecare.domain.serviceRequest.dto.req.ConsumerServiceRequest;
import jaega.homecare.domain.serviceRequest.dto.res.GetCreateServiceResponse;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequestStatus;
import jaega.homecare.domain.serviceRequest.mapper.ServiceRequestMapper;
import jaega.homecare.domain.serviceRequest.repository.ServiceRequestRepository;
import jaega.homecare.domain.users.entity.User;
import jaega.homecare.domain.users.service.query.UserQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceRequestCommandService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final UserQueryService userQueryService;
    private final ServiceRequestMapper serviceRequestMapper;

    @Transactional
    public GetCreateServiceResponse createServiceRequest(ConsumerServiceRequest request){
        User user = userQueryService.getUser(request.userId());

        ServiceRequest serviceRequest = serviceRequestMapper.toEntity(request);

        serviceRequest.setServiceRequest(UUID.randomUUID(), user, ServiceRequestStatus.PENDING, request.requestedDays());
        serviceRequestRepository.save(serviceRequest);

        return serviceRequestMapper.toGetCreateResponse(serviceRequest);
    }
}
