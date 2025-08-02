package jaega.homecare.domain.serviceRequest.service.query;

import jaega.homecare.domain.serviceRequest.dto.res.GetServiceRequestById;
import jaega.homecare.domain.serviceRequest.dto.res.GetServiceRequestResponse;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequestStatus;
import jaega.homecare.domain.serviceRequest.mapper.ServiceRequestMapper;
import jaega.homecare.domain.serviceRequest.repository.ServiceRequestRepository;
import jaega.homecare.domain.users.entity.User;
import jaega.homecare.domain.users.service.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceRequestQueryService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final UserQueryService userQueryService;
    private final ServiceRequestMapper serviceRequestMapper;

    public ServiceRequest getServiceRequest(UUID serviceRequestId){
        return serviceRequestRepository.findByServiceRequestId(serviceRequestId)
                .orElseThrow(() -> new NoSuchElementException(("서비스 요청 정보가 없습니다.")));
    }


    public List<ServiceRequest> getServiceRequestsByUser(User user){
        List<ServiceRequest> requests = serviceRequestRepository.findByUser(user);
        if (requests.isEmpty()) {
            throw new NoSuchElementException("해당 유저의 서비스 요청 정보가 없습니다.");
        }
        return requests;
    }

    public List<ServiceRequest> getServiceRequestByUserAndStatus(User user, ServiceRequestStatus status){
        List<ServiceRequest> requests = serviceRequestRepository.findAllByUserAndStatus(user, status);
        if (requests.isEmpty()) {
            throw new NoSuchElementException("해당 유저의 서비스 요청 정보가 없습니다.");
        }
        return requests;
    }

    public List<GetServiceRequestResponse> findConsumerRequests(UUID userId){
        User user = userQueryService.getUser(userId);
        List<ServiceRequest> serviceRequests = getServiceRequestsByUser(user);

        return serviceRequests.stream()
                .map(serviceRequestMapper::toFindResponseDto)
                .collect(Collectors.toList());
    }

    public List<GetServiceRequestResponse> findConsumerRequestsByStatus(UUID userId, ServiceRequestStatus status){
        User user = userQueryService.getUser(userId);
        List<ServiceRequest> serviceRequests = getServiceRequestByUserAndStatus(user, status);

        return serviceRequests.stream()
                .map(serviceRequestMapper::toFindResponseDto)
                .collect(Collectors.toList());
    }

    public GetServiceRequestById findServiceRequestById(UUID serviceRequestId){
        ServiceRequest serviceRequest = getServiceRequest(serviceRequestId);
        return serviceRequestMapper.toGetResponseById(serviceRequest);
    }

}
