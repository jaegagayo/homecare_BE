package jaega.homecare.domain.serviceRequest.service.query;

import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.consumer.service.query.ConsumerQueryService;
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
    private final ConsumerQueryService consumerQueryService;
    private final ServiceRequestMapper serviceRequestMapper;

    public ServiceRequest getServiceRequest(UUID serviceRequestId){
        return serviceRequestRepository.findByServiceRequestId(serviceRequestId)
                .orElseThrow(() -> new NoSuchElementException(("서비스 요청 정보가 없습니다.")));
    }


    public List<ServiceRequest> getServiceRequestsByUser(Consumer consumer){
        List<ServiceRequest> requests = serviceRequestRepository.findByConsumer(consumer);
        if (requests.isEmpty()) {
            throw new NoSuchElementException("해당 유저의 서비스 요청 정보가 없습니다.");
        }
        return requests;
    }

    public List<ServiceRequest> getServiceRequestByUserAndStatus(Consumer consumer, ServiceRequestStatus requestStatus){
        List<ServiceRequest> requests = serviceRequestRepository.findAllByConsumerAndRequestStatus(consumer, requestStatus);
        if (requests.isEmpty()) {
            throw new NoSuchElementException("해당 유저의 서비스 요청 정보가 없습니다.");
        }
        return requests;
    }

    public List<GetServiceRequestResponse> findConsumerRequests(UUID consumerId){
        Consumer consumer = consumerQueryService.getConsumer(consumerId);
        List<ServiceRequest> serviceRequests = getServiceRequestsByUser(consumer);

        return serviceRequests.stream()
                .map(serviceRequestMapper::toFindResponseDto)
                .collect(Collectors.toList());
    }

    public List<GetServiceRequestResponse> findConsumerRequestsByStatus(UUID consumerId, ServiceRequestStatus status){
        Consumer consumer = consumerQueryService.getConsumer(consumerId);
        List<ServiceRequest> serviceRequests = getServiceRequestByUserAndStatus(consumer, status);

        return serviceRequests.stream()
                .map(serviceRequestMapper::toFindResponseDto)
                .collect(Collectors.toList());
    }

    public GetServiceRequestById findServiceRequestById(UUID serviceRequestId){
        ServiceRequest serviceRequest = getServiceRequest(serviceRequestId);
        return serviceRequestMapper.toGetResponseById(serviceRequest);
    }

}
