package jaega.homecare.domain.WorkMatch.controller;

import jaega.homecare.domain.WorkMatch.dto.res.GetCaregiverMatchesResponse;
import jaega.homecare.domain.WorkMatch.service.query.WorkMatchQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/schedule")
public class WorkMatchControllerImpl implements WorkMatchController{

    private final WorkMatchQueryService workMatchQueryService;

    @Override
    public ResponseEntity<List<GetCaregiverMatchesResponse>> getWorkMatchByCaregiver(@PathVariable UUID caregiverId) {
        List<GetCaregiverMatchesResponse> responses = workMatchQueryService.getWorkMatchesByCaregiver(caregiverId);
        return ResponseEntity.ok(responses);
    }
}
