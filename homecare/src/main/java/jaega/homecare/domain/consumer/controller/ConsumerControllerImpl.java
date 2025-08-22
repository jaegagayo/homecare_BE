package jaega.homecare.domain.consumer.controller;

import jaega.homecare.domain.consumer.dto.res.ConsumerScheduleDetailResponse;
import jaega.homecare.domain.consumer.dto.res.ConsumerScheduleResponse;
import jaega.homecare.domain.consumer.service.query.ConsumerQueryService;
import jaega.homecare.domain.settlement.service.command.SettlementCommandService;
import jaega.homecare.domain.consumer.dto.req.ConfirmCaregiverRequest;
import jaega.homecare.domain.consumer.dto.req.ConsumerSignupRequest;
import jaega.homecare.domain.serviceMatch.dto.req.CreateServiceMatchRequest;
import jaega.homecare.domain.serviceMatch.service.command.ServiceMatchCommandService;
import jaega.homecare.domain.serviceMatch.service.query.ServiceMatchQueryService;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import jaega.homecare.domain.serviceRequest.service.query.ServiceRequestQueryService;
import jaega.homecare.domain.consumer.service.command.ConsumerCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/consumer")
public class ConsumerControllerImpl implements ConsumerController {

    private final ConsumerCommandService consumerCommandService;
    private final ServiceMatchCommandService serviceMatchCommandService;
    private final ServiceRequestQueryService serviceRequestQueryService;
    private final ConsumerQueryService consumerQueryService;

    @Override
    public ResponseEntity<Void> createConsumer(@RequestBody ConsumerSignupRequest request) {
        consumerCommandService.signupConsumer(request);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> confirmCaregiver(@RequestBody ConfirmCaregiverRequest request){

        ServiceRequest serviceRequest = serviceRequestQueryService.getServiceRequest(request.serviceRequestId());

        serviceMatchCommandService.createServiceMatch(
                new CreateServiceMatchRequest(
                        request.serviceRequestId(),
                        request.caregiverId(),
                        serviceRequest.getPreferredStartTime(),
                        serviceRequest.getPreferredEndTime(),
                        serviceRequest.getRequestDate()
                )
        );
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<ConsumerScheduleResponse>> getConsumerSchedule(@PathVariable UUID consumerId){
        LocalDate today = LocalDate.now();
        List<ConsumerScheduleResponse> responses = consumerQueryService.getConsumerSchedule(consumerId, today);
        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<ConsumerScheduleDetailResponse> getScheduleDetail(@PathVariable UUID id){
        ConsumerScheduleDetailResponse response = consumerQueryService.getScheduleDetail(id);
        return ResponseEntity.ok(response);
    }

}
