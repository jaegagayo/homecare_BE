package jaega.homecare.domain.match.controller;

import jaega.homecare.domain.match.dto.res.MatchResponse;
import jaega.homecare.domain.match.service.CaregiverMatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/match")
public class MatchControllerImpl implements MatchController {

    private final CaregiverMatchingService caregiverMatchingService;
    @Override
    public ResponseEntity<MatchResponse> matchingProcess(UUID serviceRequestId) {
         MatchResponse response = caregiverMatchingService.recommendCaregivers(serviceRequestId);
         return ResponseEntity.ok(response);
    }
}
