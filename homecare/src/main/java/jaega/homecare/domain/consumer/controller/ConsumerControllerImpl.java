package jaega.homecare.domain.consumer.controller;

import jaega.homecare.domain.WorkMatch.dto.req.CreateWorkMatchRequest;
import jaega.homecare.domain.WorkMatch.service.command.WorkMatchCommandService;
import jaega.homecare.domain.consumer.dto.req.ConfirmCaregiverRequest;
import jaega.homecare.domain.consumer.dto.req.ConsumerCreateRequest;
import jaega.homecare.domain.users.entity.UserRole;
import jaega.homecare.domain.consumer.service.command.ConsumerCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/consumer")
public class ConsumerControllerImpl implements ConsumerController {

    private final ConsumerCommandService consumerCommandService;
    private final WorkMatchCommandService workMatchCommandService;

    @Override
    public ResponseEntity<Void> createConsumer(@RequestBody ConsumerCreateRequest request) {
        consumerCommandService.createUser(request, UserRole.ROLE_CONSUMER);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> confirmCaregiver(@RequestBody ConfirmCaregiverRequest request){

        CreateWorkMatchRequest createWorkMatchRequest = new CreateWorkMatchRequest(request.caregiverId(),
                request.workTime_start(),
                request.workTime_end(),
                request.working_days(),
                request.distanceLog());
        workMatchCommandService.createWorkMatch(createWorkMatchRequest);

        return ResponseEntity.noContent().build();
    }

}
