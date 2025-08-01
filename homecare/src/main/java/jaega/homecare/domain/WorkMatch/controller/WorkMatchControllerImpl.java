package jaega.homecare.domain.WorkMatch.controller;

import jaega.homecare.domain.WorkMatch.dto.res.GetCaregiverMatchesByMonth;
import jaega.homecare.domain.WorkMatch.dto.res.GetCaregiverMatchesResponse;
import jaega.homecare.domain.WorkMatch.service.query.WorkMatchQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/centerSchedule")
public class WorkMatchControllerImpl implements WorkMatchController{

    private final WorkMatchQueryService workMatchQueryService;

    @Override
    public ResponseEntity<List<GetCaregiverMatchesResponse>> getWorkMatchByCaregiver(@PathVariable UUID caregiverId) {
        List<GetCaregiverMatchesResponse> responses = workMatchQueryService.getWorkMatchesByCaregiver(caregiverId);
        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<List<GetCaregiverMatchesByMonth>> getMatchesByMonth(
            @RequestParam int year,
            @RequestParam int month
    ) {
        List<GetCaregiverMatchesByMonth> response = workMatchQueryService.getWorkMatchesByMonth(year, month);
        return ResponseEntity.ok(response);
    }
}
