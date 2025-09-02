package jaega.homecare.domain.match.service;

import jaega.homecare.domain.match.dto.req.ServiceRequestDTO;
import jaega.homecare.domain.match.dto.res.CaregiverInfo;
import jaega.homecare.domain.match.dto.res.MatchingResponse;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import jaega.homecare.domain.serviceRequest.service.query.ServiceRequestQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final RestTemplate restTemplate;
    private final ServiceRequestQueryService serviceRequestQueryService;

    public MatchingResponse callRecommendLogging(UUID serviceRequestId) {
        String url = "http://localhost:8000/matching/recommend-logging";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ServiceRequest sr = serviceRequestQueryService.getServiceRequest(serviceRequestId);


        Map<String, Object> location = new HashMap<>();
        location.put("latitude", sr.getLocation().getLatitude());
        location.put("longitude", sr.getLocation().getLongitude());

        ServiceRequestDTO dto = new ServiceRequestDTO(
                sr.getServiceRequestId(),
                sr.getConsumer().getConsumerId(),
                sr.getServiceAddress(),
                sr.getAddressType().toString(),
                location,
                sr.getRequestDate().toString(),
                sr.getPreferredStartTime().toString(),
                sr.getPreferredEndTime().toString(),
                sr.getDuration(),
                sr.getServiceType().toString(),
                sr.getAdditionalInformation()
        );

        Map<String, Object> body = Map.of("serviceRequest", dto);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<MatchingResponse> response = restTemplate.postForEntity(url, request, MatchingResponse.class);

        MatchingResponse originalResponse = response.getBody();

        if (originalResponse == null) {
            return null; // 혹은 예외 처리
        }

        String serviceType = sr.getServiceType().toString();

        List<CaregiverInfo> updatedCaregivers = originalResponse.matchedCaregivers().stream()
                .map(c -> new CaregiverInfo(
                        c.caregiverId(),
                        c.name(),
                        c.distanceKm(),
                        c.estimatedTravelTime(),
                        c.matchScore(),
                        c.matchReason(),
                        c.address(),
                        c.addressType(),
                        c.location(),
                        c.career(),
                        c.selfIntroduction(),
                        c.isVerified(),
                        serviceType // 여기가 핵심
                ))
                .toList();

        MatchingResponse updatedResponse = new MatchingResponse(
                originalResponse.serviceRequestId(),
                updatedCaregivers,
                originalResponse.totalCandidates(),
                originalResponse.matchedCount(),
                originalResponse.processingTimeMs()
        );

        return updatedResponse;
    }
}