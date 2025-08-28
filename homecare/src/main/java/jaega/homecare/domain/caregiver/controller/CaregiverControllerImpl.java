package jaega.homecare.domain.caregiver.controller;

import jaega.homecare.domain.caregiver.dto.req.CaregiverSignupRequest;
import jaega.homecare.domain.caregiver.dto.req.ChoiceCaregiverCenterRequest;
import jaega.homecare.domain.caregiver.dto.res.GetCaregiverSignupResponse;
import jaega.homecare.domain.caregiver.dto.res.GetCaregiverVerifiedStatusResponse;
import jaega.homecare.domain.caregiver.dto.res.SelectableCaregiverCenter;
import jaega.homecare.domain.caregiver.service.command.CaregiverCommandService;
import jaega.homecare.domain.caregiver.service.query.CaregiverQueryService;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverCenter;
import jaega.homecare.domain.caregiverCenter.service.query.CaregiverCenterQueryService;
import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import jaega.homecare.domain.serviceMatch.service.query.ServiceMatchQueryService;
import jaega.homecare.domain.settlement.dto.req.CreateSettlementRequest;
import jaega.homecare.domain.settlement.service.command.SettlementCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/caregiver")
public class CaregiverControllerImpl implements CaregiverController {

    private final SettlementCommandService settlementCommandService;
    private final ServiceMatchQueryService serviceMatchQueryService;
    private final CaregiverCenterQueryService caregiverCenterQueryService;
    private final CaregiverCommandService caregiverCommandService;
    private final CaregiverQueryService caregiverQueryService;

    // 요양보호사 회원가입
    @Override
    public ResponseEntity<GetCaregiverSignupResponse> signupCaregiver(@RequestBody CaregiverSignupRequest request){
        GetCaregiverSignupResponse response = caregiverCommandService.signupCaregiver(request);
        return ResponseEntity.ok(response);
    }

    // 요양 보호사 승인 상태 조회
    // TODO : 인증 기능 구현 시 다른 페이지로 이동 못하도록 CORS 도입 고려
    @Override
    public ResponseEntity<GetCaregiverVerifiedStatusResponse> getCaregiverVerifiedStatus(@RequestParam UUID caregiverId){
        GetCaregiverVerifiedStatusResponse response = caregiverQueryService.getCaregiverVerifiedStatus(caregiverId);
        return ResponseEntity.ok(response);
    }

    /**
     * 센터 관련 API
     */

    // 소속 중인 센터 목록 조회
    @Override
    public ResponseEntity<List<SelectableCaregiverCenter>> getMyActiveCenters(@RequestParam UUID caregiverId) {
        List<CaregiverCenter> centers = caregiverCenterQueryService.getActiveCaregiverCenters(caregiverId);

        List<SelectableCaregiverCenter> response = centers.stream()
                .map(c -> new SelectableCaregiverCenter(
                        c.getCaregiverCenterId(),
                        c.getCenter().getName(),
                        c.getCenter().getUser().getPhone()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    // 요양보호사의 명시적 센터 선택
    @Override
    public ResponseEntity<Void> chooseCaregiverCenter(@RequestBody ChoiceCaregiverCenterRequest request) {
        ServiceMatch serviceMatch = serviceMatchQueryService.getServiceMatch(request.serviceMatchId());


        settlementCommandService.createSettlement(
                new CreateSettlementRequest(
                        request.caregiverCenterId(),
                        serviceMatch.getServiceMatchId(),
                        request.distanceLog()
                )
        );

        return ResponseEntity.noContent().build();
    }

    /**
     * 일정 조회 API
     */



}
