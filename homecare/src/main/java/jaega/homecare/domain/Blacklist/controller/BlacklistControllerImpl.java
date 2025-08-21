package jaega.homecare.domain.Blacklist.controller;

import jaega.homecare.domain.Blacklist.dto.req.CreateCaregiverBlacklistRequest;
import jaega.homecare.domain.Blacklist.dto.res.GetCaregiverBlacklistResponse;
import jaega.homecare.domain.Blacklist.service.command.BlacklistCommandService;
import jaega.homecare.domain.Blacklist.service.query.BlacklistQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/blacklist")
public class BlacklistControllerImpl implements BlacklistController {

    private final BlacklistCommandService blacklistCommandService;
    private final BlacklistQueryService blacklistQueryService;

    @Override
    public ResponseEntity<UUID> createCaregiverBlacklist(@RequestBody CreateCaregiverBlacklistRequest request) {
        UUID blacklistId = blacklistCommandService.createCaregiverBlacklist(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(blacklistId);
    }

    @Override
    public ResponseEntity<Void> deleteCaregiverBlacklist(@PathVariable UUID caregiverBlacklistId){
        blacklistCommandService.deleteCaregiverBlacklist(caregiverBlacklistId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<GetCaregiverBlacklistResponse>> getCaregiverBlacklistsByConsumer(@PathVariable UUID consumerId) {
        List<GetCaregiverBlacklistResponse> responses = blacklistQueryService.getCaregiverBlacklistsByConsumer(consumerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }
}