package jaega.homecare.domain.serviceRequest.controller;

import jaega.homecare.domain.serviceRequest.dto.req.ConsumerServiceRequest;
import jaega.homecare.domain.serviceRequest.service.command.ServiceRequestCommandService;
import jaega.homecare.domain.serviceRequest.service.query.ServiceRequestQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
