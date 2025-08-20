package jaega.homecare.domain.serviceRequest.service.command;

import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.consumer.service.query.ConsumerQueryService;
import jaega.homecare.domain.serviceRequest.dto.req.ConsumerServiceRequest;
import jaega.homecare.domain.serviceRequest.dto.res.GetCreateServiceResponse;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequestStatus;
import jaega.homecare.domain.serviceRequest.mapper.ServiceRequestMapper;
import jaega.homecare.domain.serviceRequest.repository.ServiceRequestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceRequestCommandService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final ConsumerQueryService consumerQueryService;
    private final ServiceRequestMapper serviceRequestMapper;

    @Transactional
    public GetCreateServiceResponse createServiceRequest(ConsumerServiceRequest request){
        Consumer consumer = consumerQueryService.getConsumer(request.consumerId());

        ServiceRequest serviceRequest = serviceRequestMapper.toEntity(request);

        serviceRequest.setServiceRequest(UUID.randomUUID(), consumer, ServiceRequestStatus.PENDING, request.requestDate());
        serviceRequestRepository.save(serviceRequest);

        return serviceRequestMapper.toGetCreateResponse(serviceRequest);
    }
}
