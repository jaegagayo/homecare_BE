package jaega.homecare.domain.consumer.controller;

import jaega.homecare.domain.WorkMatch.dto.req.CreateWorkMatchRequest;
import jaega.homecare.domain.WorkMatch.service.command.WorkMatchCommandService;
import jaega.homecare.domain.consumer.dto.req.ConfirmCaregiverRequest;
import jaega.homecare.domain.consumer.dto.req.ConsumerCreateRequest;
import jaega.homecare.domain.serviceMatch.dto.req.CreateServiceMatchRequest;
import jaega.homecare.domain.serviceMatch.dto.res.GetServiceMatchByConsumerResponse;
import jaega.homecare.domain.serviceMatch.service.command.ServiceMatchCommandService;
import jaega.homecare.domain.serviceMatch.service.query.ServiceMatchQueryService;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import jaega.homecare.domain.serviceRequest.service.query.ServiceRequestQueryService;
import jaega.homecare.domain.users.entity.UserRole;
import jaega.homecare.domain.consumer.service.command.ConsumerCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/consumer")
public class ConsumerControllerImpl implements ConsumerController {

    private final ConsumerCommandService consumerCommandService;
    private final ServiceMatchCommandService serviceMatchCommandService;
    private final WorkMatchCommandService workMatchCommandService;
    private final ServiceMatchQueryService serviceMatchQueryService;
    private final ServiceRequestQueryService serviceRequestQueryService;

    @Override
    public ResponseEntity<Void> createConsumer(@RequestBody ConsumerCreateRequest request) {
        consumerCommandService.createUser(request, UserRole.ROLE_CONSUMER);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> confirmCaregiver(@RequestBody ConfirmCaregiverRequest request){

        ServiceRequest serviceRequest = serviceRequestQueryService.getServiceRequest(request.serviceRequestId());

        CreateServiceMatchRequest createServiceMatchRequest = new CreateServiceMatchRequest(request.serviceRequestId(),
                request.caregiverId(),
                serviceRequest.getPreferred_time_start(),
                serviceRequest.getPreferred_time_end(),
                serviceRequest.getRequestedDays());
        serviceMatchCommandService.createServiceMatch(createServiceMatchRequest);

        CreateWorkMatchRequest createWorkMatchRequest = new CreateWorkMatchRequest(request.caregiverId(),
                serviceRequest.getPreferred_time_start(),
                serviceRequest.getPreferred_time_end(),
                serviceRequest.getRequestedDays(),
                serviceRequest.getAddress(),
                request.distanceLog());
        workMatchCommandService.createWorkMatch(createWorkMatchRequest);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<GetServiceMatchByConsumerResponse>> getMatchesByConsumer(@PathVariable UUID consumerId){
        List<GetServiceMatchByConsumerResponse> responses = serviceMatchQueryService.getMatchesByConsumer(consumerId);
        return ResponseEntity.ok(responses);
    }

}
