package jaega.homecare.domain.caregiverBlacklist.controller;

import jaega.homecare.domain.caregiverBlacklist.dto.req.CreateCaregiverBlacklistRequest;
import jaega.homecare.domain.caregiverBlacklist.dto.res.GetCaregiverBlacklistResponse;
import jaega.homecare.domain.caregiverBlacklist.service.command.CaregiverBlacklistCommandService;
import jaega.homecare.domain.caregiverBlacklist.service.query.CaregiverBlacklistQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/caregiver-blacklist")
public class CaregiverBlacklistControllerImpl implements CaregiverBlacklistController {

    private final CaregiverBlacklistCommandService caregiverBlacklistCommandService;
    private final CaregiverBlacklistQueryService caregiverBlacklistQueryService;

    @Override
    public ResponseEntity<UUID> createCaregiverBlacklist(@RequestBody CreateCaregiverBlacklistRequest request) {
        UUID blacklistId = caregiverBlacklistCommandService.createCaregiverBlacklist(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(blacklistId);
    }

    @Override
    public ResponseEntity<List<GetCaregiverBlacklistResponse>> getCaregiverBlacklistsByReporterId(@PathVariable UUID reporterId) {
        List<GetCaregiverBlacklistResponse> responses = caregiverBlacklistQueryService.getCaregiverBlacklistsByReporterId(reporterId);
        return ResponseEntity.ok(responses);
    }
}