package jaega.homecare.domain.match.controller;

import jaega.homecare.domain.match.dto.res.MatchingResponse;
import jaega.homecare.domain.match.dto.res.MatchingResponseDTO;
import jaega.homecare.domain.match.service.CaregiverMatchingService;
import jaega.homecare.domain.match.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/match")
public class MatchControllerImpl implements MatchController {

    private final CaregiverMatchingService caregiverMatchingService;
    private final MatchingService matchingService;
    private final RestTemplate restTemplate;

    @Override
    public ResponseEntity<MatchingResponseDTO> matchingProcess(UUID serviceRequestId) {

        return null;
    }



    @Override
    public ResponseEntity<MatchingResponse> matchingProcessLogging(UUID serviceRequestId) {
        // FastAPI 호출
        MatchingResponse loggingResult = matchingService.callRecommendLogging(serviceRequestId);


        return ResponseEntity.ok(loggingResult);
    }
    /*
    @Override
    public ResponseEntity<MatchingResponseDTO> matchingProcess(UUID serviceRequestId) {

         MatchingResponseDTO response = caregiverMatchingService.recommendCaregivers(serviceRequestId);
         return ResponseEntity.ok(response);



        return null;
    }
    */
}
