package jaega.homecare.domain.match.controller;

import jaega.homecare.domain.match.dto.req.MatchRequest;
import jaega.homecare.domain.match.dto.res.MatchingResponseDTO;
import jaega.homecare.domain.match.infra.MatchingGrpcClient;
import jaega.homecare.domain.match.service.CaregiverMatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/match")
public class MatchControllerImpl implements MatchController {

    private final CaregiverMatchingService caregiverMatchingService;

    /*
    private final MatchingGrpcClient grpcClient;

    @PostMapping("/recommend")
    public ResponseEntity<?> getRecommendations(@RequestBody MatchRequest dto) {
        // DTO를 gRPC 메시지로 변환
        MatchRequest grpcRequest = convertToGrpcRequest(dto);

        // gRPC 서버에 요청
        MatchingResponseDTO response = grpcClient.getMatchingRecommendations(grpcRequest);

        // 응답을 REST API 형태로 변환하여 반환
        return ResponseEntity.ok(convertToRestResponse(response));
    }

     */

    @Override
    public ResponseEntity<MatchingResponseDTO> matchingProcess(UUID serviceRequestId) {
        /*
         MatchingResponseDTO response = caregiverMatchingService.recommendCaregivers(serviceRequestId);
         return ResponseEntity.ok(response);

         */

        return null;
    }
}
