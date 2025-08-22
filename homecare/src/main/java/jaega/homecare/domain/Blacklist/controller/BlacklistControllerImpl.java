package jaega.homecare.domain.Blacklist.controller;

import jaega.homecare.domain.Blacklist.dto.req.CreateBlacklistByConsumerRequest;
import jaega.homecare.domain.Blacklist.dto.res.GetBlacklistByConsumerResponse;
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

    /**
     *
     * 블랙리스트 생성 API
     */
    @Override
    public ResponseEntity<UUID> createBlacklistByConsumer(@RequestBody CreateBlacklistByConsumerRequest request) {
        UUID blacklistId = blacklistCommandService.createBlacklistByConsumer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(blacklistId);
    }

    /**
     *
     * 블랙리스트 해제 API
     */
    @Override
    public ResponseEntity<Void> deleteBlacklistByConsumer(@PathVariable UUID caregiverBlacklistId){
        blacklistCommandService.deleteBlacklistByConsumer(caregiverBlacklistId);
        return ResponseEntity.noContent().build();
    }

    /**
     *
     * 신고자별 블랙리스트 조회 API
     */
    @Override
    public ResponseEntity<List<GetBlacklistByConsumerResponse>> getBlacklistByConsumer(@PathVariable UUID consumerId) {
        List<GetBlacklistByConsumerResponse> responses = blacklistQueryService.getBlacklistByConsumer(consumerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }
}