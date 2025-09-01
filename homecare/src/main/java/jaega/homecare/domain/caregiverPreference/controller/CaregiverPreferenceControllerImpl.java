package jaega.homecare.domain.caregiverPreference.controller;

import jaega.homecare.domain.caregiverPreference.dto.req.CreateCaregiverPreferenceRequest;
import jaega.homecare.domain.caregiverPreference.dto.req.UpdateCaregiverPreferenceRequest;
import jaega.homecare.domain.caregiverPreference.dto.res.GetCaregiverPreferenceResponse;
import jaega.homecare.domain.caregiverPreference.entity.CaregiverPreference;
import jaega.homecare.domain.caregiverPreference.service.command.CaregiverPreferenceCommandService;
import jaega.homecare.domain.caregiverPreference.service.query.CaregiverPreferenceQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/caregiver/preference")
public class CaregiverPreferenceControllerImpl implements CaregiverPreferenceController{

    private final CaregiverPreferenceCommandService caregiverPreferenceCommandService;
    private final CaregiverPreferenceQueryService caregiverPreferenceQueryService;

    @Override
    public ResponseEntity<Void> createCaregiverPreference(
            @RequestParam UUID caregiverId,
            @RequestBody CreateCaregiverPreferenceRequest request){
        caregiverPreferenceCommandService.createCaregiverPreference(request, caregiverId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> updateCaregiverPreference(
            @RequestParam UUID caregiverId,
            @RequestBody UpdateCaregiverPreferenceRequest request){
        CaregiverPreference caregiverPreference = caregiverPreferenceQueryService.findCaregiverPreferenceByCaregiver(caregiverId);
        caregiverPreferenceCommandService.updateCaregiverPreference(caregiverPreference, request);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<GetCaregiverPreferenceResponse> getCaregiverPreferenceByCaregiver(@RequestParam UUID caregiverId){
        GetCaregiverPreferenceResponse response = caregiverPreferenceQueryService.getCaregiverPreferenceByCaregiver(caregiverId);
        return ResponseEntity.ok(response);
    }

}
