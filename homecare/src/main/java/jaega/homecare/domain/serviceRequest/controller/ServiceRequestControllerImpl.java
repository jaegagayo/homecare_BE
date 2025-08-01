package jaega.homecare.domain.serviceRequest.controller;

import jaega.homecare.domain.serviceRequest.dto.req.ConsumerServiceRequest;
import jaega.homecare.domain.serviceRequest.dto.res.GetServiceRequestResponse;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequestStatus;
import jaega.homecare.domain.serviceRequest.service.command.ServiceRequestCommandService;
import jaega.homecare.domain.serviceRequest.service.query.ServiceRequestQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/consumer/request")
public class ServiceRequestControllerImpl implements ServiceRequestController{
    private final ServiceRequestCommandService serviceRequestCommandService;
    private final ServiceRequestQueryService serviceRequestQueryService;

    @Override
    public ResponseEntity<Void> createServiceRequest(@RequestBody ConsumerServiceRequest request){
        serviceRequestCommandService.createServiceRequest(request);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<GetServiceRequestResponse>> getConsumerServiceRequest(@RequestParam UUID userId){
        List<GetServiceRequestResponse> response = serviceRequestQueryService.findConsumerRequests(userId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<GetServiceRequestResponse>> getConsumerServiceRequestByStatus(@RequestParam UUID userId, ServiceRequestStatus status){
        List<GetServiceRequestResponse> response = serviceRequestQueryService.findConsumerRequestsByStatus(userId, status);
        return ResponseEntity.ok(response);
    }

}
