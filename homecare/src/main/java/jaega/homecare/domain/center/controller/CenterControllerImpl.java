package jaega.homecare.domain.center.controller;

import jaega.homecare.domain.WorkMatch.dto.res.GetCaregiverMatchesByMonth;
import jaega.homecare.domain.WorkMatch.dto.res.GetCaregiverMatchesResponse;
import jaega.homecare.domain.WorkMatch.service.query.WorkMatchQueryService;
import jaega.homecare.domain.caregiver.dto.req.CreateCertificationRequest;
import jaega.homecare.domain.caregiver.dto.res.GetCertificationResponse;
import jaega.homecare.domain.caregiver.entity.CaregiverStatus;
import jaega.homecare.domain.caregiver.service.command.CaregiverCommandService;
import jaega.homecare.domain.caregiver.service.command.CertificationCommandService;
import jaega.homecare.domain.caregiver.service.query.CaregiverQueryService;
import jaega.homecare.domain.caregiver.service.query.CertificationQueryService;
import jaega.homecare.domain.center.dto.req.*;
import jaega.homecare.domain.center.dto.res.*;
import jaega.homecare.domain.center.entity.Center;
import jaega.homecare.domain.center.service.command.CenterCommandService;
import jaega.homecare.domain.center.service.query.CenterQueryService;
import jaega.homecare.domain.serviceMatch.dto.res.GetServiceMatchByCenterResponse;
import jaega.homecare.domain.serviceMatch.dto.res.GetServiceMatchByUUID;
import jaega.homecare.domain.serviceMatch.service.query.ServiceMatchQueryService;
import jaega.homecare.domain.users.entity.ServiceType;
import jaega.homecare.domain.users.entity.User;
import jaega.homecare.domain.users.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/center")
public class CenterControllerImpl implements CenterController{

    private final CenterCommandService centerCommandService;
    private final CenterQueryService centerQueryService;
    private final CaregiverCommandService caregiverCommandService;
    private final CaregiverQueryService caregiverQueryService;
    private final ServiceMatchQueryService serviceMatchQueryService;
    private final WorkMatchQueryService workMatchQueryService;
    private final CertificationCommandService certificationCommandService;
    private final CertificationQueryService certificationQueryService;

    @Override
    public ResponseEntity<Void> createCaregiver(@RequestBody CreateCaregiverRequest createCaregiverRequest, @PathVariable UUID centerId){
        User user = centerCommandService.createUser(createCaregiverRequest, UserRole.ROLE_CAREGIVER);
        Center center = centerQueryService.getCenterByUUID(centerId);
        caregiverCommandService.createCaregiver(createCaregiverRequest, user, center);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<CenterLoginResponse> loginCenter(@RequestBody CenterLoginRequest request){
        CenterLoginResponse response = centerCommandService.loginCenter(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> createCaregiverProfile(@RequestBody CreateCaregiverProfileRequest request){
        caregiverCommandService.createCaregiverProfile(request);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<GetCaregiverResponse>> getAllCaregivers(@PathVariable UUID centerId){
        List<GetCaregiverResponse> caregivers = caregiverQueryService.getAllCaregiversByCenter(centerId);
        return ResponseEntity.ok(caregivers);
    }

    @Override
    public ResponseEntity<List<GetServiceMatchByCenterResponse>> getAllMatchingResult(@PathVariable UUID centerId){
        List<GetServiceMatchByCenterResponse> notifications = serviceMatchQueryService.getMatchesByCenter(centerId);
        return ResponseEntity.ok(notifications);
    }

    @Override
    public ResponseEntity<List<GetCaregiverMatchesResponse>> getWorkMatchByCaregiver(@PathVariable UUID caregiverId) {
        List<GetCaregiverMatchesResponse> responses = workMatchQueryService.getWorkMatchesByCaregiver(caregiverId);
        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<List<GetCaregiverMatchesByMonth>> getMatchesByMonth(
            @RequestParam UUID centerId,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(required = false) Integer day
    ) {
        List<GetCaregiverMatchesByMonth> response = workMatchQueryService.getWorkMatchesByMonth(centerId, year, month, day);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<GetServiceMatchByUUID> getMatchesByUUID(@PathVariable UUID serviceMatchId){
        GetServiceMatchByUUID response = serviceMatchQueryService.getMatchesByUUID(serviceMatchId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<GetCaregiverByCaregiverStatusResponse>> getCaregiversByWorkStatus(@PathVariable UUID centerId,
                                                                                                 @RequestParam CaregiverStatus caregiverStatus) {
        List<GetCaregiverByCaregiverStatusResponse> response = caregiverQueryService.getCaregiverByWorkStatus(centerId, caregiverStatus);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<GetCaregiverByServiceTypeResponse>> getCaregiverByServiceType(@PathVariable UUID centerId,
                                                                                             @RequestParam Set<ServiceType> serviceTypes){
        List<GetCaregiverByServiceTypeResponse> responses = caregiverQueryService.getCaregiverByServiceType(centerId, serviceTypes);
        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<Void> createCertification(@RequestBody CreateCertificationRequest request){
        certificationCommandService.createCertification(request);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<GetCertificationResponse> getCertificationByCaregiver(@PathVariable UUID caregiverId){
        GetCertificationResponse response = certificationQueryService.getCertificationByCaregiver(caregiverId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> changeTrainStatus(@RequestBody UUID certificationId){
        certificationCommandService.changeTrainStatus(certificationId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<GetCaregiverProfileResponse >getCaregiverProfile(@RequestParam UUID caregiverId){
        GetCaregiverProfileResponse response = caregiverQueryService.getCaregiverProfile(caregiverId);
        return ResponseEntity.ok(response);
    }
}
