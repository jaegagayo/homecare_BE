package jaega.homecare.domain.caregiverPreference.controller;

import jaega.homecare.domain.caregiverPreference.dto.req.CreateCaregiverPreferenceRequest;
import jaega.homecare.domain.caregiverPreference.service.command.CaregiverPreferenceCommandService;
import jaega.homecare.domain.caregiverPreference.service.query.CaregiverPreferenceQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/caregiver/preference")
public class CaregiverPreferenceControllerImpl implements CaregiverPreferenceController{

    private final CaregiverPreferenceCommandService caregiverPreferenceCommandService;
    private final CaregiverPreferenceQueryService caregiverPreferenceQueryService;

    @Override
    public ResponseEntity<Void> createCaregiverPreference(
            @PathVariable UUID caregiverId,
            @RequestBody CreateCaregiverPreferenceRequest request){
        caregiverPreferenceCommandService.createCaregiverPreference(request, caregiverId);
        return ResponseEntity.noContent().build();
    }

}
