package jaega.homecare.domain.center.controller;

import jaega.homecare.domain.caregiver.service.command.CaregiverCommandService;
import jaega.homecare.domain.caregiver.service.query.CaregiverQueryService;
import jaega.homecare.domain.center.dto.req.CreateCaregiverRequest;
import jaega.homecare.domain.center.dto.res.GetCaregiverResponse;
import jaega.homecare.domain.center.entity.Center;
import jaega.homecare.domain.center.service.command.CenterCommandService;
import jaega.homecare.domain.center.service.query.CenterQueryService;
import jaega.homecare.domain.users.entity.User;
import jaega.homecare.domain.users.entity.UserRole;
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
@RequestMapping("/api/center")
public class CenterControllerImpl implements CenterController{

    private final CenterCommandService centerCommandService;
    private final CenterQueryService centerQueryService;
    private final CaregiverCommandService caregiverCommandService;
    private final CaregiverQueryService caregiverQueryService;

    @Override
    public ResponseEntity<Void> createCaregiver(@RequestBody CreateCaregiverRequest createCaregiverRequest, @PathVariable UUID centerId){
        User user = centerCommandService.createUser(createCaregiverRequest, UserRole.ROLE_CAREGIVER);
        Center center = centerQueryService.getCenterByUUID(centerId);
        caregiverCommandService.createCaregiver(createCaregiverRequest, user, center);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<GetCaregiverResponse>> getAllCaregivers(@PathVariable UUID centerId){
        List<GetCaregiverResponse> caregivers = caregiverQueryService.getAllCaregiversByCenter(centerId);
        return ResponseEntity.ok(caregivers);
    }
}
